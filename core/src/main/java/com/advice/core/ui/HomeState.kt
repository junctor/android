package com.advice.core.ui

import com.advice.core.local.Conference
import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle
import com.advice.core.local.wifi.WirelessNetwork

sealed class HomeState {
    data object Loading : HomeState()

    data class Loaded(
        val conferences: List<Conference>,
        val conference: Conference,
        val menu: Menu,
        val news: NewsArticle?,
        val countdown: Long = -1L,
        val isUpdateAvailable: Boolean = false,
        val wifi: List<WirelessNetwork>,
        val hasChicken: Boolean = false,
    ) : HomeState()

    data class Error(
        val ex: Exception,
    ) : HomeState()
}
