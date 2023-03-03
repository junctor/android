package com.advice.schedule.database.repository

import com.advice.data.UserSession

class SettingsRepository(
    private val userSession: UserSession,
) {

    val conference = userSession.getConference()

}