package com.advice.data.sources

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Session
import kotlinx.coroutines.flow.Flow

interface ContentDataSource {
    fun get(): Flow<ConferenceContent>

    suspend fun getContent(conference: String, contentId: Long): Content?
    suspend fun getEvent(conference: String, contentId: Long, sessionId: Long): Event?

    suspend fun bookmark(content: Content)
    suspend fun bookmark(session: Session)

    suspend fun isBookmarked(content: Content): Boolean
    suspend fun isBookmarked(session: Session): Boolean
}
