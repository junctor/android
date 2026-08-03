package com.advice.feedback.storage

import com.advice.core.local.feedback.FeedbackForm
import com.advice.data.network.CachedFeedbackRequest
import com.advice.data.network.CachedReportRequest
import org.junit.Assert.assertEquals
import org.junit.Test

class OfflineQueueStoreDedupeTest {
    @Test
    fun `feedback dedupe keeps latest entry for same content and form`() {
        val first = cachedFeedback(contentId = 10, formId = 1, title = "first")
        val second = cachedFeedback(contentId = 10, formId = 1, title = "second")
        val other = cachedFeedback(contentId = 11, formId = 1, title = "other")

        val result = OfflineQueueStore.dedupeFeedback(listOf(first, other), second)

        assertEquals(listOf(other, second), result)
    }

    @Test
    fun `report dedupe replaces identical endpoint and payload`() {
        val first = CachedReportRequest("https://example.com", """{"a":1}""")
        val duplicate = CachedReportRequest("https://example.com", """{"a":1}""")
        val other = CachedReportRequest("https://example.com", """{"a":2}""")

        val result = OfflineQueueStore.dedupeReport(listOf(first, other), duplicate)

        assertEquals(listOf(other, duplicate), result)
    }

    private fun cachedFeedback(
        contentId: Long?,
        formId: Long,
        title: String,
    ) = CachedFeedbackRequest(
        contentId = contentId,
        feedbackForm =
            FeedbackForm(
                id = formId,
                conference = 1,
                title = title,
                items = emptyList(),
                endpoint = "https://example.com/feedback",
            ),
    )
}
