package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.analytics.core.AnalyticsProvider
import com.advice.schedule.data.repositories.SettingsRepository
import com.advice.ui.preview.Preferences
import com.advice.ui.screens.SettingsScreenPreference
import com.advice.ui.screens.SettingsScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<SettingsRepository>()
    private val analytics by inject<AnalyticsProvider>()

    private val _state = MutableStateFlow(
        SettingsScreenViewState()
    )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.state.collect {
                val preferences = listOf(
                    Preferences.ConferenceTimeZone.toPreference(it.useConferenceTimeZone)
                        .copy(title = Preferences.ConferenceTimeZone.title.replace("{timezone}", it.timezone)),
                    Preferences.ShowSchedule.toPreference(it.showSchedule),
                    Preferences.FabShown.toPreference(it.showFilterButton),
                    Preferences.AllowAnalytics.toPreference(it.enableAnalytics),
                    Preferences.EasterEggs.toPreference(it.enableEasterEggs),
                )

                _state.value = SettingsScreenViewState(
                    timeZone = it.timezone,
                    version = it.version,
                    enableEasterEggs = it.enableEasterEggs,
                    showTwitterHandle = it.showTwitterHandle,
                    preferences = preferences,
                )
            }
        }
    }

    fun onDeveloperTwitterClick(isFollow: Boolean) {
        analytics.onDeveloperEvent(isFollow)
    }

    fun onPreferenceChanged(id: String, isChecked: Boolean) {
        repository.onPreferenceChanged(id, isChecked)
    }

    fun onVersionClick() {
        analytics.onVersionClickEvent()
    }

    fun onThemeChanged(theme: String): Boolean {
        return repository.onThemeChanged(theme)
    }
}

private fun Preferences.toPreference(enabled: Boolean): SettingsScreenPreference {
    return SettingsScreenPreference(
        key = key,
        title = title,
        summary = summary,
        summaryOn = summaryOn,
        summaryOff = summaryOff,
        isChecked = enabled,
    )
}
