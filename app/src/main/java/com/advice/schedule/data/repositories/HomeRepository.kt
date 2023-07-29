package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    menuRepository: MenuRepository,
    conferencesDataSource: ConferencesDataSource,
    storage: Storage,
) {

    private val _countdown = MutableStateFlow(-1L)

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        menuRepository.menu,
        _countdown,
    ) { conference, conferences, menu, countdown ->
        HomeState.Loaded(
            forceTimeZone = storage.forceTimeZone,
            conferences = conferences,
            conference = conference,
            menu = menu.first(),
            countdown = countdown,
        )
    }

    fun setConference(conference: Conference) {
        _countdown.value = -1L
        userSession.setConference(conference)
    }

    suspend fun setCountdown(remainder: Long) {
        _countdown.emit(remainder)
    }
}
