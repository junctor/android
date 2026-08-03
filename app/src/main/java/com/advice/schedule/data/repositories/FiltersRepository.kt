package com.advice.schedule.data.repositories

import com.advice.core.local.Bookmark
import com.advice.core.local.FlowResult
import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.ui.states.FiltersScreenState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn

class FiltersRepository(
    tagsRepository: TagsRepository,
    /** In-memory filter/tag selections — not persisted event bookmarks. */
    private val filterSelectionsDataSource: BookmarkedElementDataSource,
    applicationScope: CoroutineScope,
) {
    val state =
        combine(tagsRepository.tags, filterSelectionsDataSource.get()) { typesResult, bookmarks ->
            val types =
                when (typesResult) {
                    is FlowResult.Success -> typesResult.value
                    else -> emptyList()
                }
            val filters = types.filter { it.isBrowsable && it.category == "content" }
            val isBookmarkSelected =
                bookmarks
                    .filterIsInstance<Bookmark.TagBookmark>()
                    .any { it.id == Tag.bookmark.id.toString() && it.value }
            when (typesResult) {
                FlowResult.Loading -> FiltersScreenState.Loading
                else ->
                    FiltersScreenState.Success(
                        filters = filters,
                        isBookmarkSelected = isBookmarkSelected,
                    )
            }
        }.shareIn(
            scope = applicationScope,
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun toggle(tag: Tag) {
        filterSelectionsDataSource.bookmark(tag, !tag.isSelected)
    }

    /**
     * Clears schedule filter selections only. Must use the filter-selections store
     * ([com.advice.data.sources.BookmarkDataSourceQualifiers.FILTER_SELECTIONS]);
     * must not clear persisted event bookmarks.
     */
    suspend fun clearFilters() {
        filterSelectionsDataSource.clear()
    }
}
