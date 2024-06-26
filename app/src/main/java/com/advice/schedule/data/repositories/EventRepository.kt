package com.advice.schedule.data.repositories

import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.data.sources.ContentDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class EventRepository(
    private val contentDataSource: ContentDataSource,
) {
    val events: Flow<List<Content>> = contentDataSource
        .get()
        .map { it.content }

    suspend fun getEvent(
        conference: String,
        id: Long,
    ): Event? {
        events.first().find { it.conference == conference && it.id == id }?.let {
            return Event(it, it.sessions.first())
        }
        return null
    }

    suspend fun bookmark(event: Event) {
        contentDataSource.bookmark(event)
    }
}
