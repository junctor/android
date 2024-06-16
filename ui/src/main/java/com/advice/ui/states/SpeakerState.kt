package com.advice.ui.states

import com.advice.core.local.Event
import com.advice.core.local.Speaker

sealed class SpeakerState {
    object Loading : SpeakerState()

    data class Success(
        val speaker: Speaker,
        val events: List<Event>,
    ) : SpeakerState()

    object Error : SpeakerState()
}
