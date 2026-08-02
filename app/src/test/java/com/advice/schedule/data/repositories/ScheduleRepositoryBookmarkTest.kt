package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.reminder.ReminderManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ScheduleRepositoryBookmarkTest {
    private val contentRepository = mockk<ContentRepository>(relaxed = true)
    private val tagsRepository = mockk<TagsRepository>(relaxed = true)
    private val reminderManager = mockk<ReminderManager>(relaxed = true)
    private val bookmarksDataSource = mockk<BookmarkedElementDataSource>(relaxed = true)

    private lateinit var subject: ScheduleRepository

    private val location = Location(1, "Track A", "A")

    @Before
    fun setUp() {
        subject =
            ScheduleRepository(
                contentRepository,
                tagsRepository,
                reminderManager,
                bookmarksDataSource,
            )
    }

    @Test
    fun `bookmark content with no sessions bookmarked bookmarks all and sets reminders`() =
        runTest {
            val sessions = listOf(session(1), session(2))
            val content = content(sessions)
            coEvery { contentRepository.isBookmarked(any<Session>()) } returns false

            subject.bookmark(content, session = null, isBookmarked = true)

            coVerify(exactly = 1) { contentRepository.bookmark(content, sessions[0]) }
            coVerify(exactly = 1) { contentRepository.bookmark(content, sessions[1]) }
            verify(exactly = 1) { reminderManager.setReminders(content, sessions[0]) }
            verify(exactly = 1) { reminderManager.setReminders(content, sessions[1]) }
            coVerify(exactly = 1) { contentRepository.bookmark(content) }
        }

    @Test
    fun `unbookmark content when all sessions bookmarked removes all and reminders`() =
        runTest {
            val sessions = listOf(session(1), session(2))
            val content = content(sessions)
            coEvery { contentRepository.isBookmarked(any<Session>()) } returns true

            subject.bookmark(content, session = null, isBookmarked = false)

            coVerify(exactly = 1) { contentRepository.bookmark(content, sessions[0]) }
            coVerify(exactly = 1) { contentRepository.bookmark(content, sessions[1]) }
            verify(exactly = 1) { reminderManager.removeReminders(content, sessions[0]) }
            verify(exactly = 1) { reminderManager.removeReminders(content, sessions[1]) }
            coVerify(exactly = 1) { contentRepository.bookmark(content) }
        }

    @Test
    fun `bookmark session sets reminder and bookmarks content when all sessions bookmarked`() =
        runTest {
            val sessions = listOf(session(1), session(2))
            val content = content(sessions)
            coEvery { contentRepository.isBookmarked(content) } returns false
            coEvery { contentRepository.isBookmarked(sessions[0]) } returns true
            coEvery { contentRepository.isBookmarked(sessions[1]) } returns true

            subject.bookmark(content, sessions[0], isBookmarked = true)

            coVerify(exactly = 1) { contentRepository.bookmark(content, sessions[0]) }
            verify(exactly = 1) { reminderManager.setReminders(content, sessions[0]) }
            coVerify(exactly = 1) { contentRepository.bookmark(content) }
        }

    @Test
    fun `unbookmark session removes reminder and unbookmarks content when none remain`() =
        runTest {
            val sessions = listOf(session(1), session(2))
            val content = content(sessions)
            coEvery { contentRepository.isBookmarked(content) } returns true
            coEvery { contentRepository.isBookmarked(any<Session>()) } returns false

            subject.bookmark(content, sessions[0], isBookmarked = false)

            coVerify(exactly = 1) { contentRepository.bookmark(content, sessions[0]) }
            verify(exactly = 1) { reminderManager.removeReminders(content, sessions[0]) }
            coVerify(exactly = 1) { contentRepository.bookmark(content) }
        }

    private fun content(sessions: List<Session>) =
        Content(
            id = 1,
            conference = "TEST",
            title = "Talk",
            description = "",
            updated = Instant.parse("2024-08-01T00:00:00Z"),
            speakers = emptyList(),
            types = emptyList(),
            urls = emptyList(),
            media = emptyList(),
            sessions = sessions,
        )

    private fun session(id: Long) =
        Session(
            id = id,
            timeZone = "UTC",
            start = Instant.parse("2024-08-10T18:00:00Z"),
            end = Instant.parse("2024-08-10T19:00:00Z"),
            location = location,
        )
}
