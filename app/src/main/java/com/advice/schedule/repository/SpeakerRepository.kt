package com.advice.schedule.repository

import com.advice.core.local.Speaker
import com.advice.data.datasource.EventsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class SpeakerRepository(
    private val eventsDataSource: EventsDataSource,
) {

    private var _speaker = MutableStateFlow<Speaker?>(null)

    val events = combine(_speaker, eventsDataSource.get()) { speaker, events ->
        if (speaker != null) {
            events.filter { it.speakers.any { it.id == speaker.id } }
        } else {
            emptyList()
        }
    }

    suspend fun setSpeaker(speaker: Speaker) {
        _speaker.emit(speaker)
    }
}