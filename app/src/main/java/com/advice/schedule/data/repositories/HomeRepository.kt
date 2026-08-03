package com.advice.schedule.data.repositories

import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.NewsArticle
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.wifi.WirelessNetwork
import com.advice.core.local.withGeneralFeedback
import com.advice.core.storage.UserPreferencesStore
import com.advice.core.ui.HomeState
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.FeedbackDataSource
import com.advice.wifi.data.repositories.WifiNetworkRepository
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    conferencesDataSource: ConferencesDataSource,
    menuRepository: MenuRepository,
    newsRepository: NewsRepository,
    networkRepository: WifiNetworkRepository,
    feedbackDataSource: FeedbackDataSource,
    private val storage: UserPreferencesStore,
    private val analyticsProvider: AnalyticsProvider,
) {
    val contents =
        combine(
            userSession.getConference(),
            conferencesDataSource.get(),
            menuRepository.get(),
            newsRepository.get(),
            combine(networkRepository.get(), feedbackDataSource.get()) { wifi, forms ->
                wifi to forms
            },
        ) { conference, conferences, menu, newsResult, wifiAndForms ->
            val (wifi, forms) = wifiAndForms
            val news =
                when (newsResult) {
                    is FlowResult.Success -> newsResult.value
                    else -> emptyList()
                }
            val latest =
                news.firstOrNull().takeUnless {
                    it == null || storage.hasReadNews(conference.code, it.id)
                }
            val list =
                when (conferences) {
                    is FlowResult.Failure -> return@combine HomeState.Error(conferences.error)
                    FlowResult.Loading -> return@combine HomeState.Loading
                    is FlowResult.Success -> conferences.value
                }

            HomeState.Loaded(
                conferences = list,
                conference = conference,
                menu = getMenu(menu, conference, wifi, forms),
                news = latest,
                hasChicken = hasChicken(conference),
            )
        }

    private fun hasChicken(conference: Conference): Boolean =
        conference.code == "DEFCON33" && storage.easterEggs && analyticsProvider.isChickenEnabled()

    private fun getMenu(
        menu: FlowResult<List<Menu>>,
        conference: Conference,
        wifi: List<WirelessNetwork>,
        forms: List<FeedbackForm>,
    ): Menu =
        when (menu) {
            is FlowResult.Failure -> Menu.ERROR
            FlowResult.Loading -> Menu.LOADING
            is FlowResult.Success -> menu(menu, conference, wifi, forms)
        }

    private fun menu(
        result: FlowResult.Success<List<Menu>>,
        conference: Conference,
        wifi: List<WirelessNetwork>,
        forms: List<FeedbackForm>,
    ): Menu {
        if (result.value.isEmpty()) return Menu.ERROR
        val menu =
            (result.value.find { it.id == conference.homeMenuId } ?: result.value.first())
                .withGeneralFeedback(forms)
        if (!analyticsProvider.isWifiEnabled() || wifi.isEmpty()) {
            return menu
        }
        val wifiItems =
            wifi.map { network ->
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

    fun refreshConference() {
        userSession.currentConference?.let { userSession.setConference(it) }
    }
}
