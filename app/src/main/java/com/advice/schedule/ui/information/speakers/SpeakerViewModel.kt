package com.advice.schedule.ui.information.speakers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Speaker
import com.advice.schedule.repository.SpeakerRepository
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class SpeakerViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SpeakerRepository>()

    val events = repository.events

    fun setSpeaker(speaker: Speaker) {
        viewModelScope.launch {
            repository.setSpeaker(speaker)
        }
    }
}