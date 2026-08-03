package com.advice.settings.di

import com.advice.settings.data.repositories.SettingsRepository
import com.advice.settings.presentation.viewmodel.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

fun settingsModule(version: String) =
    module {
        single {
            SettingsRepository(
                get(),
                get(),
                version,
                androidContext(),
                get(),
                get(),
            )
        }
        viewModel { SettingsViewModel(get(), get()) }
    }
