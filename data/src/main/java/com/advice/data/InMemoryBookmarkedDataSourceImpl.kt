package com.advice.data

import com.advice.core.local.Bookmark
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryBookmarkedDataSourceImpl : BookmarkedElementDataSource {

    private val flow = MutableStateFlow<List<Bookmark>>(emptyList())

    private val map = mutableMapOf<Long, Bookmark>()

    override fun get() = flow

    override suspend fun bookmark(tag: Tag, isBookmarked: Boolean) {
        if (isBookmarked) {
            map[tag.id] = Bookmark(tag.id.toString(), value = true)
        } else {
            map.remove(tag.id)
        }
        flow.emit(map.values.toList())
    }

    override suspend fun bookmark(content: Content, isBookmarked: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun bookmark(session: Session, isBookmarked: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun isBookmarked(content: Content): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isBookmarked(session: Session): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun isBookmarked(tag: Tag): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun clear() {
        map.clear()
        flow.emit(map.values.toList())
    }
}
