package com.advice.schedule.data.repositories

import com.advice.core.local.Speaker
import com.advice.data.sources.SpeakersDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class SpeakersRepository(
    private val speakersDataSource: SpeakersDataSource,
) {
    val speakers: Flow<List<Speaker>> = speakersDataSource
        .get()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )

    suspend fun get(id: Long): Speaker? = speakersDataSource.get(id)
}
