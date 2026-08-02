package com.advice.schedule.data.repositories

import com.advice.core.local.Bookmark
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.ui.FiltersScreenState
import com.advice.data.sources.BookmarkedElementDataSource
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FiltersRepositoryStateTest {
    private val tagsRepository = mockk<TagsRepository>()
    private val bookmarksDataSource = mockk<BookmarkedElementDataSource>()

    private val browsableContent =
        TagType(
            id = 1,
            label = "Event Type",
            category = "content",
            isBrowsable = true,
            sortOrder = 0,
            tags = listOf(Tag(10, "Talk", "", "#FF0000", 0)),
        )
    private val nonBrowsableContent =
        TagType(
            id = 2,
            label = "Hidden",
            category = "content",
            isBrowsable = false,
            sortOrder = 1,
            tags = emptyList(),
        )
    private val merch =
        TagType(
            id = 3,
            label = "Sizes",
            category = "merch-variant",
            isBrowsable = true,
            sortOrder = 2,
            tags = emptyList(),
        )

    @Test
    fun `state filters to browsable content category types only`() =
        runTest {
            stubTags(listOf(browsableContent, nonBrowsableContent, merch))
            every { bookmarksDataSource.get() } returns flowOf(emptyList())

            val subject = FiltersRepository(tagsRepository, bookmarksDataSource)
            val state = awaitState(subject) as FiltersScreenState.Success

            assertEquals(listOf(browsableContent), state.filters)
            assertFalse(state.isBookmarkSelected)
        }

    @Test
    fun `bookmark selected from TagBookmark`() =
        runTest {
            stubTags(listOf(browsableContent))
            every { bookmarksDataSource.get() } returns
                flowOf(listOf(Bookmark.TagBookmark(Tag.bookmark.id.toString(), value = true)))

            val subject = FiltersRepository(tagsRepository, bookmarksDataSource)
            val state = awaitState(subject) as FiltersScreenState.Success

            assertTrue(state.isBookmarkSelected)
        }

    private fun stubTags(tags: List<TagType>) {
        every { tagsRepository.tags } returns
            MutableSharedFlow<List<TagType>>(replay = 1).apply {
                tryEmit(tags)
            }
    }

    private fun awaitState(subject: FiltersRepository): FiltersScreenState {
        var attempts = 0
        while (subject.state.replayCache.isEmpty() && attempts < 50) {
            // shareIn uses Dispatchers.IO; wall-clock wait (runTest delay is virtual).
            Thread.sleep(20)
            attempts++
        }
        require(subject.state.replayCache.isNotEmpty()) { "state.replayCache stayed empty" }
        return subject.state.replayCache.first()
    }
}
