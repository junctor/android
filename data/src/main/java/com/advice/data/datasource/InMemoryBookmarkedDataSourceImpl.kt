package com.advice.data.datasource

import com.advice.core.local.Bookmark
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryBookmarkedDataSourceImpl : BookmarkedElementDataSource {

    private val flow = MutableStateFlow<List<Bookmark>>(emptyList())

    private val map = mutableMapOf<Long, Bookmark>()

    override fun get() = flow

    override suspend fun clear() {
        map.clear()
        flow.emit(map.values.toList())
    }

    override suspend fun bookmark(id: Long, isBookmarked: Boolean) {
        map[id] = Bookmark(id.toString(), value = isBookmarked)
        flow.emit(map.values.toList())
    }
}