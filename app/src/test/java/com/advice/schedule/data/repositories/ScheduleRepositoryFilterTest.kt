package com.advice.schedule.data.repositories

import com.advice.core.local.Bookmark
import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.ui.ScheduleFilter
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.schedule.domain.ContentBookmarkUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class ScheduleRepositoryFilterTest {
    private val contentRepository = mockk<ContentRepository>()
    private val tagsRepository = mockk<TagsRepository>()
    private val contentBookmarkUseCase = mockk<ContentBookmarkUseCase>(relaxed = true)
    private val bookmarksDataSource = mockk<BookmarkedElementDataSource>()

    private val locationA = Location(1, "Track A", "A")
    private val locationB = Location(2, "Track B", "B")

    private val talk = Tag(10, "Talk", "", "#FF0000", 0)
    private val workshop = Tag(11, "Workshop", "", "#00FF00", 1)
    private val beginner = Tag(20, "Beginner", "", "#0000FF", 0, isSelected = true)
    private val advanced = Tag(21, "Advanced", "", "#FFFF00", 1)

    private lateinit var subject: ScheduleRepository

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
    fun `default with no selected tags returns all events`() =
        runTest {
            stubContent(listOf(content(1, listOf(session(1, locationA), session(2, locationB)))))
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Default).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(2, (result as ScheduleResult.Success).events.size)
        }

    @Test
    fun `default with bookmark filter and no tags returns bookmarked sessions only`() =
        runTest {
            stubContent(
                listOf(
                    content(
                        1,
                        listOf(
                            session(1, locationA, bookmarked = true),
                            session(2, locationB, bookmarked = false),
                        ),
                    ),
                ),
            )
            stubTags(emptyList())
            stubBookmarks(listOf(Bookmark.TagBookmark(Tag.bookmark.id.toString(), true)))

            val result = subject.getSchedule(ScheduleFilter.Default).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(listOf(1L), (result as ScheduleResult.Success).events.map { it.session.id })
        }

    @Test
    fun `default applies AND across tag groups`() =
        runTest {
            val talkSelected = talk.copy(isSelected = true)
            val beginnerSelected = beginner.copy(isSelected = true)
            stubContent(
                listOf(
                    content(1, listOf(session(1, locationA)), types = listOf(talkSelected, beginnerSelected)),
                    content(2, listOf(session(2, locationA)), types = listOf(talkSelected, advanced)),
                    content(3, listOf(session(3, locationA)), types = listOf(workshop, beginnerSelected)),
                ),
            )
            stubTags(
                listOf(
                    TagType(1, "Type", "content", true, 0, listOf(talkSelected, workshop)),
                    TagType(2, "Level", "content", true, 1, listOf(beginnerSelected, advanced)),
                ),
            )
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Default).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(listOf(1L), (result as ScheduleResult.Success).events.map { it.content.id })
        }

    @Test
    fun `location filter returns matching location only`() =
        runTest {
            stubContent(
                listOf(
                    content(1, listOf(session(1, locationA), session(2, locationB))),
                ),
            )
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Location(locationA.id)).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(listOf(1L), (result as ScheduleResult.Success).events.map { it.session.id })
        }

    @Test
    fun `tag filter by bookmark id returns bookmarked sessions`() =
        runTest {
            stubContent(
                listOf(
                    content(
                        1,
                        listOf(
                            session(1, locationA, bookmarked = true),
                            session(2, locationB, bookmarked = false),
                        ),
                    ),
                ),
            )
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Tag(Tag.bookmark.id, "Bookmarks")).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(listOf(1L), (result as ScheduleResult.Success).events.map { it.session.id })
        }

    @Test
    fun `tag filter by type id returns matching content`() =
        runTest {
            stubContent(
                listOf(
                    content(1, listOf(session(1, locationA)), types = listOf(talk)),
                    content(2, listOf(session(2, locationA)), types = listOf(workshop)),
                ),
            )
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Tag(talk.id, "Talk")).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(listOf(1L), (result as ScheduleResult.Success).events.map { it.content.id })
        }

    @Test
    fun `tags filter with bookmark id only returns bookmarked sessions`() =
        runTest {
            stubContent(
                listOf(
                    content(
                        1,
                        listOf(
                            session(1, locationA, bookmarked = true),
                            session(2, locationB, bookmarked = false),
                        ),
                    ),
                ),
            )
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Tags(listOf(Tag.bookmark.id))).first()

            assertTrue(result is ScheduleResult.Success)
            assertEquals(listOf(1L), (result as ScheduleResult.Success).events.map { it.session.id })
        }

    @Test
    fun `empty bookmark filter returns bookmark message`() =
        runTest {
            stubContent(listOf(content(1, listOf(session(1, locationA)))))
            stubTags(emptyList())
            stubBookmarks(listOf(Bookmark.TagBookmark(Tag.bookmark.id.toString(), true)))

            val result = subject.getSchedule(ScheduleFilter.Default).first()

            assertTrue(result is ScheduleResult.Empty)
            assertEquals("Bookmark events to see them here", (result as ScheduleResult.Empty).message)
        }

    @Test
    fun `empty location filter returns location message`() =
        runTest {
            stubContent(listOf(content(1, listOf(session(1, locationA)))))
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Location(99)).first()

            assertTrue(result is ScheduleResult.Empty)
            assertEquals("No events found in this location", (result as ScheduleResult.Empty).message)
        }

    @Test
    fun `empty tag filter returns label message`() =
        runTest {
            stubContent(listOf(content(1, listOf(session(1, locationA)), types = listOf(talk))))
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Tag(workshop.id, "Workshop")).first()

            assertTrue(result is ScheduleResult.Empty)
            assertEquals("No events found for Workshop", (result as ScheduleResult.Empty).message)
        }

    @Test
    fun `empty content emits loading`() =
        runTest {
            stubContent(emptyList())
            stubTags(emptyList())
            stubBookmarks(emptyList())

            val result = subject.getSchedule(ScheduleFilter.Default).first()

            assertTrue(result is ScheduleResult.Loading)
        }

    private fun stubContent(items: List<Content>) {
        every { contentRepository.content } returns
            MutableSharedFlow<ConferenceContent>(replay = 1).apply {
                tryEmit(ConferenceContent(items))
            }
    }

    private fun stubTags(tags: List<TagType>) {
        every { tagsRepository.tags } returns
            MutableSharedFlow<List<TagType>>(replay = 1).apply {
                tryEmit(tags)
            }
    }

    private fun stubBookmarks(bookmarks: List<Bookmark>) {
        every { bookmarksDataSource.get() } returns flowOf(bookmarks)
    }

    private fun content(
        id: Long,
        sessions: List<Session>,
        types: List<Tag> = emptyList(),
    ) = Content(
        id = id,
        conference = "TEST",
        title = "Content $id",
        description = "",
        updated = Instant.parse("2024-08-01T00:00:00Z"),
        speakers = emptyList(),
        types = types,
        urls = emptyList(),
        media = emptyList(),
        sessions = sessions,
    )

    private fun session(
        id: Long,
        location: Location,
        bookmarked: Boolean = false,
        start: Instant = Instant.parse("2024-08-10T18:00:00Z"),
    ) = Session(
        id = id,
        timeZone = "UTC",
        start = start,
        end = start.plusSeconds(3600),
        location = location,
        isBookmarked = bookmarked,
    )
}
