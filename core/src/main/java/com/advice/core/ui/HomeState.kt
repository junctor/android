package com.advice.core.ui

import com.advice.core.local.Conference
import com.advice.core.local.Document
import com.advice.core.local.Menu
import com.advice.core.local.NewsArticle

sealed class HomeState {
    object Loading : HomeState()
    data class Loaded(
        val conferences: List<Conference>,
        val conference: Conference,
        val menu: List<Menu>,
        val countdown: Long = -1L,
        val forceTimeZone: Boolean,
    ) : HomeState()

    data class Error(val ex: Exception) : HomeState()
}
