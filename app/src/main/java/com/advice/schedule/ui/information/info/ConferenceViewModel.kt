package com.advice.schedule.ui.information.info

import androidx.lifecycle.ViewModel
import com.advice.data.UserSession
import org.koin.core.KoinComponent
import org.koin.core.inject

class ConferenceViewModel : ViewModel(), KoinComponent {

    private val userSession by inject<UserSession>()

    val conference = userSession.conference

}