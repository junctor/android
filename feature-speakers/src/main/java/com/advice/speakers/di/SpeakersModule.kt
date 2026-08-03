package com.advice.speakers.di

import com.advice.speakers.data.repositories.SpeakerRepository
import com.advice.speakers.data.repositories.SpeakersRepository
import com.advice.speakers.presentation.viewmodel.SpeakerViewModel
import com.advice.speakers.presentation.viewmodel.SpeakersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val speakersModule =
    module {
        single { SpeakersRepository(get()) }
        single { SpeakerRepository(get(), get()) }
        viewModel { SpeakersViewModel(get()) }
        viewModel { SpeakerViewModel(get()) }
    }
