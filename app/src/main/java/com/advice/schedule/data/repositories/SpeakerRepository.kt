package com.advice.schedule.data.repositories

import com.advice.core.local.Event
import com.advice.ui.states.SpeakerState
import kotlinx.coroutines.flow.first

class SpeakerRepository(
    private val speakersRepository: SpeakersRepository,
    private val contentRepository: ContentRepository,
) {
    suspend fun getSpeakerDetails(id: Long): SpeakerState {
        val speaker = speakersRepository.get(id) ?: return SpeakerState.Error

        val events = contentRepository.content.first().content.filter {
            it.speakers.any { it.id == id }
        }.flatMap {
            it.sessions.map { session ->
                Event(it, session)
            }
        }
        return SpeakerState.Success(speaker, events)
    }
}
