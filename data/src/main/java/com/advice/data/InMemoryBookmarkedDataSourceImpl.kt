package com.advice.data

import com.advice.core.local.Bookmark
import com.advice.data.sources.BookmarkedElementDataSource
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
        if(isBookmarked) {
            map[id] = Bookmark(id.toString(), value = true)
        } else {
            map.remove(id)
        }
        flow.emit(map.values.toList())
    }
}