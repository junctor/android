package com.advice.schedule.data.repositories

import com.advice.core.local.Speaker
import com.advice.data.sources.SpeakersDataSource
import kotlinx.coroutines.flow.Flow

class SpeakersRepository(
    private val speakersDataSource: SpeakersDataSource,
) {
    /** Upstream speakers datasource is already a StateFlow on the application scope. */
    val speakers: Flow<List<Speaker>> = speakersDataSource.get()

    suspend fun get(id: Long): Speaker? = speakersDataSource.get(id)
}
