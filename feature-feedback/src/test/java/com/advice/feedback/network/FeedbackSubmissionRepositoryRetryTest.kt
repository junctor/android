package com.advice.feedback.network

import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.network.CachedFeedbackRequest
import com.advice.data.storage.UserPreferencesStore
import com.advice.feedback.storage.OfflineQueueStore
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FeedbackSubmissionRepositoryRetryTest {
    private val preferences =
        mockk<UserPreferencesStore> {
            every { userUUID } returns "device-uuid"
        }
    private val offlineQueue = mockk<OfflineQueueStore>(relaxed = true)

    @Test
    fun `retryCached removes corrupt entries with blank endpoint`() =
        runTest {
            val entry =
                CachedFeedbackRequest(
                    contentId = 7L,
                    feedbackForm =
                        FeedbackForm(
                            id = 1,
                            conference = 1,
                            title = "Form",
                            items = emptyList(),
                            endpoint = "",
                        ),
                )
            every { offlineQueue.getCachedFeedbackRequest() } returns listOf(entry)

            FeedbackSubmissionRepository("1.0", preferences, offlineQueue).retryCached()

            verify(exactly = 1) { offlineQueue.removeCachedFeedbackRequest(entry) }
            verify(exactly = 0) { offlineQueue.storeFeedbackRequest(any()) }
        }

    @Test
    fun `retryCached failure does not requeue or remove`() =
        runTest {
            val entry =
                CachedFeedbackRequest(
                    contentId = 7L,
                    feedbackForm =
                        FeedbackForm(
                            id = 1,
                            conference = 1,
                            title = "Form",
                            items = emptyList(),
                            // Connection refused — post fails without re-queuing.
                            endpoint = "http://127.0.0.1:1",
                        ),
                )
            every { offlineQueue.getCachedFeedbackRequest() } returns listOf(entry)

            FeedbackSubmissionRepository("1.0", preferences, offlineQueue).retryCached()

            verify(exactly = 0) { offlineQueue.removeCachedFeedbackRequest(any()) }
            verify(exactly = 0) { offlineQueue.storeFeedbackRequest(any()) }
        }
}
