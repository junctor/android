package com.advice.data

import com.advice.core.local.Bookmark
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import kotlinx.coroutines.flow.MutableStateFlow

class InMemoryBookmarkedDataSourceImpl : BookmarkedElementDataSource {

    private val flow = MutableStateFlow<List<Bookmark>>(emptyList())

    private val map = mutableMapOf<String, Bookmark>()

    override fun get() = flow

    override suspend fun bookmark(tag: Tag, isBookmarked: Boolean) {
        bookmark(key = "tag:${tag.id}", Bookmark.TagBookmark(tag.id.toString(), value = true), isBookmarked)
    }

    override suspend fun bookmark(content: Content, isBookmarked: Boolean) {
        bookmark(
            key = "content:${content.id}",
            Bookmark.ContentBookmark(content.id.toString(), value = true),
            isBookmarked,
        )
    }

    override suspend fun bookmark(session: Session, isBookmarked: Boolean) {
        bookmark(
            key = "session:${session.id}",
            Bookmark.SessionBookmark(session.id.toString(), value = true),
            isBookmarked,
        )
    }

    private suspend fun bookmark(key: String, bookmark: Bookmark, isBookmarked: Boolean) {
        if (isBookmarked) {
            map[key] = bookmark
        } else {
            map.remove(key)
        }
        flow.emit(map.values.toList())
    }

    override suspend fun isBookmarked(content: Content): Boolean {
        return map.containsKey("content:${content.id}")
    }

    override suspend fun isBookmarked(session: Session): Boolean {
        return map.containsKey("session:${session.id}")
    }

    override suspend fun isBookmarked(tag: Tag): Boolean {
        return map.containsKey("tag:${tag.id}")
    }

    override suspend fun clear() {
        map.clear()
        flow.emit(map.values.toList())
    }
}
