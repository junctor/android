package com.advice.schedule.data.repositories

import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.TagsDataSource


class FiltersRepository(
    private val tagsDataSource: TagsDataSource,
    private val bookmarksDataSource: BookmarkedElementDataSource,
) {

    val tags = tagsDataSource.get()

    suspend fun toggle(tag: Tag) {
        bookmarksDataSource.bookmark(tag.id, !tag.isSelected)
    }

    suspend fun clear() {
        bookmarksDataSource.clear()
    }

}