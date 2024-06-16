package com.advice.schedule.data.repositories

import com.advice.core.local.Speaker
import com.advice.data.sources.SpeakersDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn

class SpeakersRepository(
    speakersDataSource: SpeakersDataSource,
) {
    val speakers: SharedFlow<List<Speaker>> = speakersDataSource
        .get()
        .shareIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            replay = 1,
        )
}
