package com.advice.data.sources

import com.advice.core.local.Event
import kotlinx.coroutines.flow.Flow

interface EventsDataSource {

    fun get(): Flow<List<Event>>

    suspend fun bookmark(event: Event)
}
