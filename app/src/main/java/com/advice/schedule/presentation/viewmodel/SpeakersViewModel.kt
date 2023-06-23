package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.SpeakersRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SpeakersRepository>()

    val speakers = repository.list

}