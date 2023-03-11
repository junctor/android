package com.advice.schedule.repository

import com.advice.data.UserSession

class InformationRepository(
    private val userSession: UserSession,
) {
    val conference = userSession.getConference()
}