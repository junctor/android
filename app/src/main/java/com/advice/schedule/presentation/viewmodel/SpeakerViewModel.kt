package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Speaker
import com.advice.schedule.data.repositories.SpeakerRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakerViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SpeakerRepository>()

    val events = repository.events

    fun setSpeaker(speaker: Speaker) {
        viewModelScope.launch {
            repository.setSpeaker(speaker)
        }
    }
}