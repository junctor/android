package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.EventsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class ContentRepository(
    private val contentDataSource: ContentDataSource,
    private val eventsDataSource: EventsDataSource,
) {
    val content = eventsDataSource
        .get()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun get(
        conference: String,
        id: Long,
    ): Event? = contentDataSource.get(conference, id)

    suspend fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
    }
}
