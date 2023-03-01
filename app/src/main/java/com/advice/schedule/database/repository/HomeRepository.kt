package com.advice.schedule.database.repository

import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.data.UserSession
import com.advice.data.datasource.ArticleDataSource
import com.advice.data.datasource.ConferencesDataSource
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    private val articleDataSource: ArticleDataSource,
    private val conferencesDataSource: ConferencesDataSource,
) {


    val contents = combine(userSession.conference, conferencesDataSource.get(), articleDataSource.get()) { conference, conferences, articles ->
        HomeState.Loaded(conferences, conference, articles)
    }

    fun setConference(conference: Conference) {
        userSession.setConference( conference)
    }
}