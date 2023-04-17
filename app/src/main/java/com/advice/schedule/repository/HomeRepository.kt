package com.advice.schedule.repository

import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.data.UserSession
import com.advice.data.datasource.ArticleDataSource
import com.advice.data.datasource.ConferencesDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    private val articleDataSource: ArticleDataSource,
    private val conferencesDataSource: ConferencesDataSource,
) {

    private val _countdown = MutableStateFlow(-1L)

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        articleDataSource.get(),
        _countdown
    ) { conference, conferences, articles, countdown ->
        HomeState.Loaded(conferences, conference, articles, countdown)
    }

    fun setConference(conference: Conference) {
        userSession.setConference(conference)
    }

    suspend fun setCountdown(remainder: Long) {
        _countdown.emit(remainder)
    }
}