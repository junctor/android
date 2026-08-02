package com.advice.locations.di

import com.advice.locations.data.repositories.LocationRepository
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val locationsModule =
    module {
        single { LocationRepository(get()) }
        viewModel { LocationsViewModel() }
    }
