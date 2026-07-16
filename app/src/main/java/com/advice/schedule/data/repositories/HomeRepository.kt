package com.advice.schedule.data.repositories

import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.NewsArticle
import com.advice.core.local.wifi.WirelessNetwork
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
    private val analyticsProvider: AnalyticsProvider,
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
            menu = getMenu(menu, conference, wifi),
            news = latest,
            hasChicken = hasChicken(conference),
        )
    }

    private fun hasChicken(conference: Conference): Boolean {
        return conference.code == "DEFCON33" && storage.easterEggs && analyticsProvider.isChickenEnabled()
    }

    private fun getMenu(
        menu: FlowResult<List<Menu>>,
        conference: Conference,
        wifi: List<WirelessNetwork>,
    ): Menu {
        return when (menu) {
            is FlowResult.Failure -> Menu.ERROR
            FlowResult.Loading -> Menu.LOADING
            is FlowResult.Success -> menu(menu, conference, wifi)
        }
    }

    private fun menu(
        result: FlowResult.Success<List<Menu>>,
        conference: Conference,
        wifi: List<WirelessNetwork>,
    ): Menu {
        if (result.value.isEmpty()) return Menu.ERROR
        val menu = result.value.find { it.id == conference.homeMenuId } ?: result.value.first()
        if (!analyticsProvider.isWifiEnabled() || wifi.isEmpty()) {
            return menu
        }
        val wifiItems = wifi.map { network ->
            MenuItem.Wifi(
                label = "WiFi",
                description = "Connect to the ${network.titleText}",
                id = network.id,
            )
        }
        return menu.copy(items = menu.items + wifiItems)
    }

    fun markLatestNewsAsRead(newsArticle: NewsArticle) {
        storage.markNewsAsRead(userSession.currentConference?.code, newsArticle.id)
    }

    fun setConference(conference: Conference) {
        userSession.setConference(conference)
    }
}
