package com.advice.schedule.data.repositories

import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class FiltersRepository(
    tagsRepository: TagsRepository,
    private val bookmarksDataSource: BookmarkedElementDataSource,
) {
    val tags = tagsRepository.tags.map {
        it.filter { it.isBrowsable && it.category == "content" }
    }.shareIn(
        scope = CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Eagerly,
        replay = 1,
    )

    suspend fun toggle(tag: Tag) {
        bookmarksDataSource.bookmark(tag.id, !tag.isSelected)
    }

    suspend fun clear() {
        bookmarksDataSource.clear()
    }
}
