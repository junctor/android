package com.advice.schedule.ui.settings

import androidx.lifecycle.ViewModel
import com.advice.core.ui.SettingsScreenState
import com.advice.schedule.repository.SettingsRepository
import com.advice.schedule.utilities.Analytics
import kotlinx.coroutines.flow.flow
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.concurrent.Flow

class SettingsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SettingsRepository>()

    private val analytics by inject<Analytics>()

    val state = repository.state

    fun onDeveloperTwitterClick(isFollow: Boolean) {
        analytics.onDeveloperEvent(isFollow)
    }

    fun onPreferenceChanged(id: Int, isChecked: Boolean) {
        repository.onPreferenceChanged(id, isChecked)
    }
}