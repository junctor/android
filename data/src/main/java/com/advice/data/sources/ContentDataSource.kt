package com.advice.data.sources

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import kotlinx.coroutines.flow.Flow

interface ContentDataSource {
    fun get(): Flow<ConferenceContent>

    suspend fun getContent(conference: String, id: Long): Content?
    suspend fun getEvent(conference: String, id: Long): Event?

    suspend fun bookmark(content: Content)
    suspend fun bookmark(event: Event)
}
