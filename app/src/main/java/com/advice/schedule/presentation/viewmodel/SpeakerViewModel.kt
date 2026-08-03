package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.schedule.data.repositories.SpeakerRepository
import com.advice.ui.states.SpeakerState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SpeakerViewModel(
    private val repository: SpeakerRepository,
) : ViewModel() {
    private val _speakerDetails = MutableStateFlow<SpeakerState>(SpeakerState.Loading)
    val speakerDetails: Flow<SpeakerState> get() = _speakerDetails

    fun fetchSpeakerDetails(id: Long?) {
        if (id == null) {
            _speakerDetails.value = SpeakerState.Error
            return
        }

        viewModelScope.launch {
            val details = repository.getSpeakerDetails(id)
            _speakerDetails.value = details
        }
    }
}
