package com.advice.core.ui

import com.advice.core.local.Conference
import com.advice.core.local.Article

sealed class HomeState {
    object Loading : HomeState()
    data class Loaded(
        val conferences: List<Conference>,
        val conference: Conference,
        val article: List<Article>,
        val countdown: Long
    ) : HomeState()
    data class Error(val ex: Exception) : HomeState()
}
