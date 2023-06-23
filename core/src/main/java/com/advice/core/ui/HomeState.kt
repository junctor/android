package com.advice.core.ui

import com.advice.core.local.Conference
import com.advice.core.local.NewsArticle

sealed class HomeState {
    object Loading : HomeState()
    data class Loaded(
        val conferences: List<Conference>,
        val conference: Conference,
        val hasWifi: Boolean = false,
        val hasProducts: Boolean = false,
        val news: List<NewsArticle>,
        val countdown: Long
    ) : HomeState()
    data class Error(val ex: Exception) : HomeState()
}
