package com.advice.schedule.data.repositories

import com.advice.core.ui.SettingsScreenState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.ui.preview.Preferences
import kotlinx.coroutines.flow.map


class SettingsRepository(
    private val userSession: UserSession,
    private val preferences: Storage,
    private val version: String,
) {
    val state = userSession.getConference().map {
        SettingsScreenState(
            it.timezone,
            version,
            preferences.forceTimeZone,
            preferences.showSchedule,
            preferences.showFilters,
            preferences.easterEggs,
            preferences.allowAnalytics,
            showTwitterHandle = false,
        )
    }

    fun onPreferenceChanged(
        id: String,
        checked: Boolean,
    ) {
        when (id) {
            Preferences.ConferenceTimeZone.key -> preferences.forceTimeZone = checked
            Preferences.ShowSchedule.key -> preferences.showSchedule = checked
            Preferences.FabShown.key -> preferences.showFilters = checked
            Preferences.AllowAnalytics.key -> preferences.allowAnalytics = checked
            Preferences.EasterEggs.key -> preferences.easterEggs = checked
        }
    }

    fun onThemeChanged(theme: String): Boolean {
        val result = preferences.theme != theme
        preferences.theme = theme
        return result
    }
}
