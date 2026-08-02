package com.advice.reminder

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.Session
import com.advice.core.local.feedback.ContentFeedbackForm
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.utils.Storage
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ReminderManagerTest {
    private val workManager = mockk<WorkManager>(relaxed = true)
    private val storage = mockk<Storage>()
    private lateinit var subject: ReminderManager

    private val location = Location(1, "Track A", "A")
    private val futureStart = Instant.now().plusSeconds(86_400)
    private val pastStart = Instant.now().minusSeconds(86_400)

    @Before
    fun setUp() {
        subject = ReminderManager(workManager, storage)
    }

    @Test
    fun disabledEventReminder_doesNotEnqueue() {
        every { storage.eventReminderMinutes } returns ReminderMinutes.DISABLED
        every { storage.feedbackReminderMinutes } returns ReminderMinutes.DISABLED

        subject.setReminders(content(feedback = null), session(futureStart))

        verify(exactly = 0) { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun futureSession_enqueuesReminder() {
        every { storage.eventReminderMinutes } returns 20
        every { storage.feedbackReminderMinutes } returns ReminderMinutes.DISABLED

        val content = content(feedback = null)
        val session = session(futureStart)
        subject.setReminders(content, session)

        verify(exactly = 1) {
            workManager.enqueueUniqueWork(
                "reminder/${content.conference}/${content.id}:${session.id}",
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>(),
            )
        }
    }

    @Test
    fun pastSession_doesNotEnqueueReminder() {
        every { storage.eventReminderMinutes } returns 20
        every { storage.feedbackReminderMinutes } returns ReminderMinutes.DISABLED

        subject.setReminders(content(feedback = null), session(pastStart))

        verify(exactly = 0) { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun noFeedbackForm_skipsFeedbackWork() {
        every { storage.eventReminderMinutes } returns ReminderMinutes.DISABLED
        every { storage.feedbackReminderMinutes } returns 0

        subject.setReminders(content(feedback = null), session(futureStart))

        verify(exactly = 0) { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun disabledFeedbackReminder_skipsFeedbackWork() {
        every { storage.eventReminderMinutes } returns ReminderMinutes.DISABLED
        every { storage.feedbackReminderMinutes } returns ReminderMinutes.DISABLED
        val enable = Instant.now().plusSeconds(86_400)

        subject.setReminders(content(feedback = feedback(enable)), session(futureStart))

        verify(exactly = 0) { workManager.enqueueUniqueWork(any(), any(), any<OneTimeWorkRequest>()) }
    }

    @Test
    fun feedbackEnabled_enqueuesFeedbackWork() {
        every { storage.eventReminderMinutes } returns ReminderMinutes.DISABLED
        every { storage.feedbackReminderMinutes } returns 0
        val enable = Instant.now().plusSeconds(86_400)
        val content = content(feedback = feedback(enable))
        val session = session(futureStart)

        subject.setReminders(content, session)

        verify(exactly = 1) {
            workManager.enqueueUniqueWork(
                "feedback/${content.conference}/${content.id}:${session.id}",
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>(),
            )
        }
    }

    @Test
    fun removeReminders_cancelsBothTags() {
        val content = content(feedback = null)
        val session = session(futureStart)

        subject.removeReminders(content, session)

        verify(exactly = 1) {
            workManager.cancelAllWorkByTag("reminder/${content.conference}/${content.id}:${session.id}")
        }
        verify(exactly = 1) {
            workManager.cancelAllWorkByTag("feedback/${content.conference}/${content.id}:${session.id}")
        }
    }

    @Test
    fun updateReminders_cancelsThenSets() {
        every { storage.eventReminderMinutes } returns 20
        every { storage.feedbackReminderMinutes } returns ReminderMinutes.DISABLED
        val content = content(feedback = null)
        val session = session(futureStart)

        subject.updateReminders(content, session)

        verify(exactly = 1) {
            workManager.cancelAllWorkByTag("reminder/${content.conference}/${content.id}:${session.id}")
        }
        verify(exactly = 1) {
            workManager.cancelAllWorkByTag("feedback/${content.conference}/${content.id}:${session.id}")
        }
        verify(exactly = 1) {
            workManager.enqueueUniqueWork(
                "reminder/${content.conference}/${content.id}:${session.id}",
                ExistingWorkPolicy.REPLACE,
                any<OneTimeWorkRequest>(),
            )
        }
    }

    private fun content(feedback: ContentFeedbackForm?) =
        Content(
            id = 42,
            conference = "TEST",
            title = "Talk",
            description = "",
            updated = Instant.parse("2024-08-01T00:00:00Z"),
            speakers = emptyList(),
            types = emptyList(),
            urls = emptyList(),
            media = emptyList(),
            sessions = emptyList(),
            feedback = feedback,
        )

    private fun session(start: Instant) =
        Session(
            id = 7,
            timeZone = "UTC",
            start = start,
            end = start.plusSeconds(3600),
            location = location,
        )

    private fun feedback(enable: Instant) =
        ContentFeedbackForm(
            enable = enable,
            disable = enable.plusSeconds(86_400),
            form =
                FeedbackForm(
                    id = 1,
                    conference = 1,
                    title = "Feedback",
                    items = emptyList(),
                    endpoint = "https://example.com",
                ),
        )
}
