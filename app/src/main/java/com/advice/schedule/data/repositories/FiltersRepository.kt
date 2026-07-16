package com.advice.schedule.data.repositories

import com.advice.core.local.Bookmark
import com.advice.core.local.Tag
import com.advice.core.ui.FiltersScreenState
import com.advice.data.sources.BookmarkedElementDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.shareIn

class FiltersRepository(
    tagsRepository: TagsRepository,
    private val bookmarksDataSource: BookmarkedElementDataSource,
) {
    val state =
        combine(tagsRepository.tags, bookmarksDataSource.get()) { types, bookmarks ->
            val filters = types.filter { it.isBrowsable && it.category == "content" }
            val isBookmarkSelected =
                bookmarks
                    .filterIsInstance<Bookmark.TagBookmark>()
                    .any { it.id == Tag.bookmark.id.toString() && it.value }
            FiltersScreenState.Success(
                filters = filters,
                isBookmarkSelected = isBookmarkSelected,
            )
        }.shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun toggle(tag: Tag) {
        bookmarksDataSource.bookmark(tag, !tag.isSelected)
    }

    suspend fun clear() {
        bookmarksDataSource.clear()
    }
}
