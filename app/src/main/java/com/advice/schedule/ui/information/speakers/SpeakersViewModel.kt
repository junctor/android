package com.advice.schedule.ui.information.speakers

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.SpeakersRepository
import org.koin.core.KoinComponent
import org.koin.core.inject

class SpeakersViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SpeakersRepository>()

    val speakers = repository.list

}