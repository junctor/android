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

        val events =
            when (val result = contentRepository.content.first()) {
                is com.advice.core.local.FlowResult.Success ->
                    result.value.content
                        .filter {
                            it.speakers.any { speaker -> speaker.id == id }
                        }.flatMap {
                            it.sessions.map { session ->
                                Event(it, session)
                            }
                        }
                else -> emptyList()
            }
        return SpeakerState.Success(speaker, events)
    }
}
