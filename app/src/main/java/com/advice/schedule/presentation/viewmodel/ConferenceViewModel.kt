package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.data.session.UserSession

class ConferenceViewModel(
    userSession: UserSession,
) : ViewModel() {
    val conference = userSession.getConference()
}
