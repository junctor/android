package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.SpeakersRepository

class SpeakersViewModel(
    private val repository: SpeakersRepository,
) : ViewModel() {
    val speakers = repository.speakers
}
