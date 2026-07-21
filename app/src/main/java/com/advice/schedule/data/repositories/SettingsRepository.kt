package com.advice.schedule.data.repositories

import android.content.Context
import com.advice.core.ui.SettingsScreenState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.schedule.telemetry.TelemetryCollection
import com.advice.ui.preview.Preferences
import kotlinx.coroutines.flow.map


class SettingsRepository(
    userSession: UserSession,
    private val preferences: Storage,
    private val version: String,
    private val context: Context,
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
            preferences.allowCrashlytics,
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
            Preferences.AllowAnalytics.key -> {
                preferences.allowAnalytics = checked
                TelemetryCollection.apply(context, preferences)
            }
            Preferences.AllowCrashlytics.key -> {
                preferences.allowCrashlytics = checked
                TelemetryCollection.apply(context, preferences)
            }
            Preferences.EasterEggs.key -> preferences.easterEggs = checked
        }
    }

    fun onThemeChanged(theme: String): Boolean {
        val result = preferences.theme != theme
        preferences.theme = theme
        return result
    }
}
