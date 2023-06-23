package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.NewsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    private val newsDataSource: NewsDataSource,
    private val conferencesDataSource: ConferencesDataSource,
) {

    private val _countdown = MutableStateFlow(-1L)

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        newsDataSource.get(),
        _countdown
    ) { conference, conferences, news, countdown ->
        val isDefCon = conference.code.contains("DEFCON")
        HomeState.Loaded(conferences, conference, isDefCon, isDefCon, news, countdown)
    }

    fun setConference(conference: Conference) {
        userSession.setConference(conference)
    }

    suspend fun setCountdown(remainder: Long) {
        _countdown.emit(remainder)
    }
}