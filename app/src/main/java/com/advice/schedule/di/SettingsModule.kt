package com.advice.schedule.di

import com.advice.schedule.data.repositories.SettingsRepository
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val settingsModule =
    module {
        single {
            SettingsRepository(
                get(),
                get(),
                "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                androidContext(),
                get(),
            )
        }
        viewModel { SettingsViewModel(get(), get()) }
    }
