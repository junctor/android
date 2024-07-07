package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.data.sources.ContentDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EventRepository(
    private val contentDataSource: ContentDataSource,
) {
    val events: Flow<List<Content>> = contentDataSource
        .get()
        .map { it.content }

    suspend fun bookmark(content: Content) {
        contentDataSource.bookmark(content)
    }

    suspend fun bookmark(session: Session) {
        contentDataSource.bookmark(session)
    }

    suspend fun isBookmarked(content: Content): Boolean {
        return contentDataSource.isBookmarked(content)
    }

    suspend fun isBookmarked(session: Session): Boolean {
        return contentDataSource.isBookmarked(session)
    }
}
