package com.advice.schedule.repository

import com.advice.core.ui.SettingsScreenState
import com.advice.data.UserSession
import com.advice.core.utils.Storage
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
            preferences.fabShown,
            preferences.easterEggs,
            preferences.allowAnalytics,
            showTwitterHandle = false,
        )
    }

    fun onPreferenceChanged(id: Int, checked: Boolean) {
        when (id) {
            1 -> preferences.forceTimeZone = checked
            2 -> preferences.fabShown = checked
            3 -> preferences.easterEggs = checked
            4 -> preferences.allowAnalytics = checked
        }
    }
}