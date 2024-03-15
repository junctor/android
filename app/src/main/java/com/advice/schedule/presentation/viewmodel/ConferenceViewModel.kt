package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.data.session.UserSession
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ConferenceViewModel : ViewModel(), KoinComponent {

    private val userSession by inject<UserSession>()

    val conference = userSession.getConference()
}
