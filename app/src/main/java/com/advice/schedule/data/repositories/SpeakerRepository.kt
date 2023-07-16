package com.advice.schedule.data.repositories

import com.advice.ui.screens.SpeakerState
import kotlinx.coroutines.flow.first

class SpeakerRepository(
    private val speakersRepository: SpeakersRepository,
    private val eventsRepository: EventsRepository,
) {
    suspend fun getSpeakerDetails(id: Long): SpeakerState {
        val speaker = speakersRepository.speakers.first().find { it.id == id }!!
        val events = eventsRepository.events.first().filter {
            it.speakers.any { it.id == id }
        }
        return SpeakerState.Success(speaker, events)
    }
}