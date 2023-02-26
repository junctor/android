package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseBookmark
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryBookmarkedDataSourceImpl: BookmarkedElementDataSource {

    private val flow = MutableStateFlow<List<FirebaseBookmark>>(emptyList())

    private val map = mutableMapOf<Long, FirebaseBookmark>()

    override fun get() = flow

    override suspend fun clear() {
        map.clear()
        flow.emit(map.values.toList())
    }

    override suspend fun bookmark(id: Long, isBookmarked: Boolean) {
        map[id] = FirebaseBookmark(id.toString(), value = isBookmarked)
        flow.emit(map.values.toList())
    }
}