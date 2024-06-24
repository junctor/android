package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.data.sources.ContentDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

class EventRepository(
    private val contentDataSource: ContentDataSource,
) {
    val events: Flow<List<Event>> = contentDataSource
        .get()
        .map { it.events }
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun getEvent(
        conference: String,
        id: Long,
    ): Event? = contentDataSource.getEvent(conference, id)

    suspend fun bookmark(event: Event) {
        contentDataSource.bookmark(event)
    }
}
