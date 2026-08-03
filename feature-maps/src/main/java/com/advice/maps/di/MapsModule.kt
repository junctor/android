package com.advice.maps.di

import com.advice.maps.data.repositories.MapsRepository
import com.advice.maps.presentation.viewmodel.MapsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mapsModule =
    module {
        single { MapsRepository(get()) }
        viewModel { MapsViewModel(get()) }
    }
