package com.advice.speakers.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.speakers.data.repositories.SpeakersRepository

class SpeakersViewModel(
    private val repository: SpeakersRepository,
) : ViewModel() {
    val speakers = repository.speakers
}
