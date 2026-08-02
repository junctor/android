package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.storage.ContentSyncStore
import com.advice.core.utils.NotificationHelper
import com.advice.data.sources.ContentDataSource
import com.advice.reminder.ReminderManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class ContentRepositoryReminderSyncTest {
    private val contentDataSource = mockk<ContentDataSource>()
    private val reminderManager = mockk<ReminderManager>(relaxed = true)
    private val notificationHelper = mockk<NotificationHelper>(relaxed = true)
    private val storage = mockk<ContentSyncStore>(relaxed = true)

    private val location = Location(1, "Track A", "A")
    private val updated = Instant.parse("2024-08-01T12:00:00Z")
    private val updatedMillis = updated.toEpochMilli()

    @Test
    fun `updated bookmark with older stored timestamp updates reminders and notifies`() =
        runTest {
            val bookmarked = session(1, bookmarked = true)
            val item = content(sessions = listOf(bookmarked))
            val flow = MutableSharedFlow<ConferenceContent>(replay = 1)
            every { contentDataSource.get() } returns flow
            every { storage.getContentUpdatedTimestamp(item.id) } returns updatedMillis - 1_000

            val subject = ContentRepository(contentDataSource, reminderManager, notificationHelper, storage)
            flow.emit(ConferenceContent(listOf(item)))
            awaitReplay(subject)
            Thread.sleep(50)

            verify(exactly = 1) { reminderManager.updateReminders(item, bookmarked) }
            verify(exactly = 1) { notificationHelper.notifySessionUpdated(any()) }
            verify(exactly = 1) { storage.setContentUpdatedTimestamp(item.id, updatedMillis) }
        }

    @Test
    fun `timestamp zero seeds without updateReminders or notify`() =
        runTest {
            val bookmarked = session(1, bookmarked = true)
            val item = content(sessions = listOf(bookmarked))
            val flow = MutableSharedFlow<ConferenceContent>(replay = 1)
            val timestamps = mutableMapOf<Long, Long>()
            every { contentDataSource.get() } returns flow
            every { storage.getContentUpdatedTimestamp(any()) } answers {
                timestamps[firstArg()] ?: 0L
            }
            every { storage.setContentUpdatedTimestamp(any(), any()) } answers {
                timestamps[firstArg()] = secondArg()
            }

            val subject = ContentRepository(contentDataSource, reminderManager, notificationHelper, storage)
            flow.emit(ConferenceContent(listOf(item)))
            awaitReplay(subject)
            Thread.sleep(50)

            verify(exactly = 1) { storage.setContentUpdatedTimestamp(item.id, updatedMillis) }
            verify(exactly = 0) { reminderManager.updateReminders(any(), any()) }
            verify(exactly = 0) { notificationHelper.notifySessionUpdated(any()) }
        }

    private fun awaitReplay(subject: ContentRepository) {
        var attempts = 0
        while (subject.content.replayCache.isEmpty() && attempts < 50) {
            // shareIn uses Dispatchers.IO; wall-clock wait (runTest delay is virtual).
            Thread.sleep(20)
            attempts++
        }
        require(subject.content.replayCache.isNotEmpty()) { "content.replayCache stayed empty" }
    }

    private fun content(sessions: List<Session>) =
        Content(
            id = 10,
            conference = "TEST",
            title = "Talk",
            description = "",
            updated = updated,
            speakers = emptyList(),
            types = emptyList(),
            urls = emptyList(),
            media = emptyList(),
            sessions = sessions,
        )

    private fun session(
        id: Long,
        bookmarked: Boolean,
    ) = Session(
        id = id,
        timeZone = "UTC",
        start = Instant.parse("2024-08-10T18:00:00Z"),
        end = Instant.parse("2024-08-10T19:00:00Z"),
        location = location,
        isBookmarked = bookmarked,
    )
}
