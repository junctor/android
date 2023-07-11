package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.NewsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    newsDataSource: NewsDataSource,
    conferencesDataSource: ConferencesDataSource,
    documentsDataSource: DocumentsDataSource,
) {

    private val _countdown = MutableStateFlow(-1L)

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        documentsDataSource.get(),
        newsDataSource.get(),
        _countdown
    ) { conference, conferences, documents, news, countdown ->
        val isDefCon = conference.code.contains("DEFCON30")
        HomeState.Loaded(conferences, conference, isDefCon, isDefCon, documents, news, countdown)
    }

    fun setConference(conference: Conference) {
        _countdown.value = -1L
        userSession.setConference(conference)
    }

    suspend fun setCountdown(remainder: Long) {
        _countdown.emit(remainder)
    }
}