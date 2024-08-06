package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle
import com.advice.core.ui.HomeState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.NewsDataSource
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    conferencesDataSource: ConferencesDataSource,
    menuRepository: MenuRepository,
    newsRepository: NewsDataSource,
    networkRepository: WifiNetworkRepository,
    private val storage: Storage,
) {

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        menuRepository.get(),
        newsRepository.get(),
        networkRepository.get(),
    ) { conference, conferences, menu, news, wifi ->
        val latest = news.firstOrNull().takeUnless {
            it == null || storage.hasReadNews(conference.code, it.id)
        }
        val list = when (conferences) {
            is FlowResult.Failure -> return@combine HomeState.Error(conferences.error)
            FlowResult.Loading -> return@combine HomeState.Loading
            is FlowResult.Success -> conferences.value
        }

        HomeState.Loaded(
            conferences = list,
            conference = conference,
            menu = getMenu(menu, conference),
            news = latest,
            wifi = wifi,
        )
    }

    private fun getMenu(
        menu: FlowResult<List<Menu>>,
        conference: Conference
    ): Menu {
        return when (menu) {
            is FlowResult.Failure -> Menu(
                -1,
                "ERROR",
                emptyList(),
            )

            FlowResult.Loading -> Menu(
                -1,
                "LOADING",
                emptyList(),
            )

            is FlowResult.Success -> menu.value.find { it.id == conference.homeMenuId }
                ?: menu.value.firstOrNull() ?: Menu(
                    -1,
                    "Nothing",
                    emptyList(),
                )
        }
    }

    fun markLatestNewsAsRead(newsArticle: NewsArticle) {
        storage.markNewsAsRead(userSession.currentConference?.code, newsArticle.id)
    }

    fun setConference(conference: Conference) {
        userSession.setConference(conference)
    }
}
