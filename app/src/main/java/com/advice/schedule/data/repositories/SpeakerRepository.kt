package com.advice.schedule.data.repositories

import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.ui.screens.SpeakerState
import kotlinx.coroutines.flow.first

class SpeakerRepository(
    private val speakersDataSource: SpeakersDataSource,
    private val eventsDataSource: EventsDataSource,
) {
    suspend fun getSpeakerDetails(id: Long): SpeakerState {
        val speaker = speakersDataSource.get().first().find { it.id == id }!!
        val events = eventsDataSource.get().first().filter {
            it.speakers.any { it.id == id }
        }
        return SpeakerState.Success(speaker, events)
    }
}