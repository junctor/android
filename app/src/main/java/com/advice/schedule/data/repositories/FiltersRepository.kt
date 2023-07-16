package com.advice.schedule.data.repositories

import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.TagsDataSource
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map


class FiltersRepository(
    tagsDataSource: TagsDataSource,
    private val bookmarksDataSource: BookmarkedElementDataSource,
) {

    val tags = tagsDataSource.get().map {
        it.filter { it.isBrowsable && it.category == "content" }
    }

    suspend fun toggle(tag: Tag) {
        bookmarksDataSource.bookmark(tag.id, !tag.isSelected)
    }

    suspend fun clear() {
        bookmarksDataSource.clear()
    }
}