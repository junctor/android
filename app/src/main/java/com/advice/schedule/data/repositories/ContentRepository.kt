package com.advice.schedule.data.repositories

import com.advice.core.local.ConferenceContent
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.EventsDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class ContentRepository(
    private val contentDataSource: ContentDataSource,
    private val eventsDataSource: EventsDataSource,
) {
    val content: Flow<ConferenceContent> = eventsDataSource
        .get()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun getEvent(
        conference: String,
        id: Long,
    ): Event? = contentDataSource.getEvent(conference, id)

    suspend fun getContent(
        conference: String,
        id: Long,
    ): Content? = contentDataSource.getContent(conference, id)

    suspend fun bookmark(event: Event) {
        eventsDataSource.bookmark(event)
    }
}
