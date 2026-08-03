package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

/**
 * Locks the dual-store contract: "Clear filters" uses the filter-selections
 * [com.advice.data.sources.BookmarkedElementDataSource] and must not clear
 * persisted event bookmarks.
 */
class FiltersRepositoryClearTest {
    private val tagsRepository = mockk<TagsRepository>()

    @Test
    fun `clearFilters empties filter selections but leaves event bookmarks intact`() =
        runTest {
            every { tagsRepository.tags } returns
                MutableSharedFlow<List<TagType>>(replay = 1).apply {
                    tryEmit(emptyList())
                }

            val filterSelections = InMemoryBookmarkedDataSourceImpl()
            val eventBookmarks = InMemoryBookmarkedDataSourceImpl()

            val filterTag = Tag(10, "Talk", "", "#FF0000", 0, isSelected = true)
            val session =
                Session(
                    id = 42,
                    timeZone = "UTC",
                    start = Instant.parse("2024-08-10T18:00:00Z"),
                    end = Instant.parse("2024-08-10T19:00:00Z"),
                    location = Location(1, "Track A", "A"),
                    isBookmarked = true,
                )
            val content =
                Content(
                    id = 7,
                    conference = "TEST",
                    title = "Talk",
                    description = "",
                    updated = Instant.parse("2024-08-01T00:00:00Z"),
                    speakers = emptyList(),
                    types = emptyList(),
                    urls = emptyList(),
                    media = emptyList(),
                    sessions = listOf(session),
                    isBookmarked = true,
                )

            filterSelections.bookmark(filterTag, true)
            eventBookmarks.bookmark(session, true)
            eventBookmarks.bookmark(content, true)

            assertTrue(filterSelections.isBookmarked(filterTag))
            assertTrue(eventBookmarks.isBookmarked(session))
            assertTrue(eventBookmarks.isBookmarked(content))

            FiltersRepository(tagsRepository, filterSelections).clearFilters()

            assertFalse(filterSelections.isBookmarked(filterTag))
            assertTrue(eventBookmarks.isBookmarked(session))
            assertTrue(eventBookmarks.isBookmarked(content))
        }
}
