package com.advice.speakers.data.repositories

import com.advice.core.local.Event
import com.advice.core.local.FlowResult
import com.advice.data.sources.ContentDataSource
import com.advice.ui.states.SpeakerState
import kotlinx.coroutines.flow.first

class SpeakerRepository(
    private val speakersRepository: SpeakersRepository,
    private val contentDataSource: ContentDataSource,
) {
    suspend fun getSpeakerDetails(id: Long): SpeakerState {
        val speaker = speakersRepository.get(id) ?: return SpeakerState.Error

        val events =
            when (val result = contentDataSource.get().first()) {
                is FlowResult.Success ->
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
