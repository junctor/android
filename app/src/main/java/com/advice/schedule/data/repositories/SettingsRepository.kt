package com.advice.schedule.data.repositories

import com.advice.core.ui.SettingsScreenState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
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
            preferences.fabShown,
            preferences.easterEggs,
            preferences.allowAnalytics,
            showTwitterHandle = false,
        )
    }

    fun onPreferenceChanged(id: String, checked: Boolean) {
        when (id) {
            "force_time_zone" -> preferences.forceTimeZone = checked
            "show_schedule" -> preferences.showSchedule = checked
            "show_filter" -> preferences.fabShown = checked
            "allow_analytics" -> preferences.allowAnalytics = checked
            "easter_eggs" -> preferences.easterEggs = checked
        }
    }
}
