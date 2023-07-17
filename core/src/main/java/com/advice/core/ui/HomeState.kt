package com.advice.core.ui

import com.advice.core.local.Conference
import com.advice.core.local.Document
import com.advice.core.local.NewsArticle

sealed class HomeState {
    object Loading : HomeState()
    data class Loaded(
        val conferences: List<Conference>,
        val conference: Conference,
        val hasWifi: Boolean = false,
        val hasProducts: Boolean = false,
        val productExample: String? = null,
        val documents: List<Document> = emptyList(),
        val news: List<NewsArticle> = emptyList(),
        val countdown: Long = -1L,
    ) : HomeState()

    data class Error(val ex: Exception) : HomeState()
}
