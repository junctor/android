package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.schedule.data.repositories.SettingsRepository
import com.advice.analytics.core.AnalyticsProvider
import org.koin.core.KoinComponent
import org.koin.core.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SettingsRepository>()

    private val analytics by inject<AnalyticsProvider>()

    val state = repository.state

    fun onDeveloperTwitterClick(isFollow: Boolean) {
        analytics.onDeveloperEvent(isFollow)
    }

    fun onPreferenceChanged(id: String, isChecked: Boolean) {
        repository.onPreferenceChanged(id, isChecked)
    }
}