package com.advice.schedule.database.repository

import com.advice.schedule.database.datasource.BookmarkedElementDataSource
import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.models.firebase.FirebaseTag


class FiltersRepository(
    private val tagsDataSource: TagsDataSource,
    private val bookmarksDataSource: BookmarkedElementDataSource) {


    val tags = tagsDataSource.get()

    suspend fun toggle(tag: FirebaseTag) {
        bookmarksDataSource.bookmark(tag.id, !tag.isSelected)
    }

    suspend fun clear() {
        bookmarksDataSource.clear()
    }

}