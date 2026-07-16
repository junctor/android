package com.advice.core.ui

import com.advice.core.local.Conference
import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle

sealed class HomeState {
    data object Loading : HomeState()

    data class Loaded(
        val conferences: List<Conference>,
        val conference: Conference,
        val menu: Menu,
        val news: NewsArticle?,
        val isUpdateAvailable: Boolean = false,
        val hasChicken: Boolean = false,
    ) : HomeState()

    data class Error(
        val ex: Exception,
    ) : HomeState()
}
