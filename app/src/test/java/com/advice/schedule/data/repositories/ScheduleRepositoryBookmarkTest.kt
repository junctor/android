package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.schedule.domain.ContentBookmarkUseCase
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ScheduleRepositoryBookmarkTest {
    private val contentRepository = mockk<ContentRepository>(relaxed = true)
    private val tagsRepository = mockk<TagsRepository>(relaxed = true)
    private val contentBookmarkUseCase = mockk<ContentBookmarkUseCase>(relaxed = true)
    private val bookmarksDataSource = mockk<BookmarkedElementDataSource>(relaxed = true)

    private lateinit var subject: ScheduleRepository

    private val location = Location(1, "Track A", "A")

    @Before
    fun setUp() {
        subject =
            ScheduleRepository(
                contentRepository,
                tagsRepository,
                contentBookmarkUseCase,
                bookmarksDataSource,
            )
    }

    @Test
    fun `bookmark delegates to ContentBookmarkUseCase`() =
        runTest {
            val sessions = listOf(session(1), session(2))
            val content = content(sessions)

            subject.bookmark(content, session = null, isBookmarked = true)

            coVerify(exactly = 1) {
                contentBookmarkUseCase.bookmark(content, session = null, isBookmarked = true)
            }
        }

    @Test
    fun `bookmark session delegates to ContentBookmarkUseCase`() =
        runTest {
            val sessions = listOf(session(1), session(2))
            val content = content(sessions)

            subject.bookmark(content, sessions[0], isBookmarked = false)

            coVerify(exactly = 1) {
                contentBookmarkUseCase.bookmark(content, sessions[0], isBookmarked = false)
            }
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
