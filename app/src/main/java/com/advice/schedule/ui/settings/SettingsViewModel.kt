package com.advice.schedule.ui.settings

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.SettingsRepository
import com.advice.schedule.utilities.Analytics
import org.koin.core.KoinComponent
import org.koin.core.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SettingsRepository>()

    private val analytics by inject<Analytics>()

    val conference = repository.conference

    fun onDeveloperTwitterClick(isFollow: Boolean) {
        analytics.onDeveloperEvent(isFollow)
    }
}