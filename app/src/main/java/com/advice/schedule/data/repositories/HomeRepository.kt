package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle
import com.advice.core.ui.HomeState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.NewsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    conferencesDataSource: ConferencesDataSource,
    menuRepository: MenuRepository,
    newsRepository: NewsDataSource,
    private val storage: Storage,
) {

    private val _countdown = MutableStateFlow(-1L)

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        menuRepository.menu,
        newsRepository.get(),
        _countdown,
    ) { conference, conferences, menu, news, countdown ->
        val latest = news.firstOrNull().takeUnless {
            it == null || storage.hasReadNews(conference.code, it.id)
        }
        HomeState.Loaded(
            forceTimeZone = storage.forceTimeZone,
            conferences = conferences,
            conference = conference,
            menu = menu.find { it.id == conference.homeMenuId } ?: menu.firstOrNull() ?: Menu(-1, "Nothing", emptyList()),
            news = latest,
            countdown = countdown,
        )
    }

    fun markLatestNewsAsRead(newsArticle: NewsArticle) {
        storage.markNewsAsRead(userSession.currentConference?.code, newsArticle.id)
    }

    fun setConference(conference: Conference) {
        _countdown.value = -1L
        userSession.setConference(conference)
    }

    suspend fun setCountdown(remainder: Long) {
        _countdown.emit(remainder)
    }
}
