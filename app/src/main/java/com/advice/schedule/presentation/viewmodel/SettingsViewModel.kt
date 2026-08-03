package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.ReminderMinutes
import com.advice.core.preferences.Preferences
import com.advice.schedule.data.repositories.SettingsRepository
import com.advice.ui.screens.SettingsScreenPreference
import com.advice.ui.screens.SettingsScreenViewState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val repository: SettingsRepository,
    private val analytics: AnalyticsProvider,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            SettingsScreenViewState(),
        )
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            repository.state.collect {
                val preferences =
                    listOf(
                        Preferences.GlitchAnimation.toPreference(it.enableGlitchAnimation),
                        Preferences.ConferenceTimeZone
                            .toPreference(it.useConferenceTimeZone)
                            .copy(title = Preferences.ConferenceTimeZone.title.replace("{timezone}", it.timezone)),
                        Preferences.ShowSchedule.toPreference(it.showSchedule),
                        Preferences.FabShown.toPreference(it.showFilterButton),
                        Preferences.AllowAnalytics.toPreference(it.enableAnalytics),
                        Preferences.AllowCrashlytics.toPreference(it.enableCrashlytics),
                        Preferences.EasterEggs.toPreference(it.enableEasterEggs),
                    )

                _state.value =
                    SettingsScreenViewState(
                        timeZone = it.timezone,
                        version = it.version,
                        enableEasterEggs = it.enableEasterEggs,
                        scheduleDayFormat = it.scheduleDayFormat,
                        eventReminderMinutes = it.eventReminderMinutes,
                        feedbackReminderMinutes = it.feedbackReminderMinutes,
                        preferences = preferences,
                    )
            }
        }
    }

    fun onPreferenceChanged(
        id: String,
        isChecked: Boolean,
    ) {
        repository.onPreferenceChanged(id, isChecked)
    }

    fun onVersionClick() {
        analytics.onVersionClickEvent()
    }

    fun onThemeChanged(theme: String): Boolean = repository.onThemeChanged(theme)

    fun onScheduleDayFormatChanged(formatId: String) {
        repository.onScheduleDayFormatChanged(formatId)
    }

    fun onEventReminderMinutesChanged(minutes: Int) {
        repository.onEventReminderMinutesChanged(ReminderMinutes.sanitizeEvent(minutes))
    }

    fun onFeedbackReminderMinutesChanged(minutes: Int) {
        repository.onFeedbackReminderMinutesChanged(ReminderMinutes.sanitizeFeedback(minutes))
    }
}

private fun Preferences.toPreference(enabled: Boolean): SettingsScreenPreference =
    SettingsScreenPreference(
        key = key,
        title = title,
        summary = summary,
        summaryOn = summaryOn,
        summaryOff = summaryOff,
        isChecked = enabled,
    )
