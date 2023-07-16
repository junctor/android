package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.data.sources.EventsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class EventsRepository(
    private val eventsDataSource: EventsDataSource,
) {
    val events = eventsDataSource.get()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
    }
}