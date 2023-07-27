package com.advice.schedule.data.repositories

import com.advice.core.local.Conference
import com.advice.core.local.Document
import com.advice.core.local.NewsArticle
import com.advice.core.local.Product
import com.advice.core.ui.HomeState
import com.advice.core.utils.Storage
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.NewsDataSource
import com.advice.data.sources.ProductsDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

class HomeRepository(
    private val userSession: UserSession,
    newsDataSource: NewsDataSource,
    conferencesDataSource: ConferencesDataSource,
    documentsDataSource: DocumentsDataSource,
    productsDataSource: ProductsDataSource,
    storage: Storage,
) {

    private val _countdown = MutableStateFlow(-1L)

    val contents = combine(
        userSession.getConference(),
        conferencesDataSource.get(),
        documentsDataSource.get(),
        newsDataSource.get(),
        productsDataSource.get(),
        _countdown,
    ) { array ->
        val conference = array[0] as Conference
        val conferences = array[1] as List<Conference>
        val documents = array[2] as List<Document>
        val news = array[3] as List<NewsArticle>
        val products = array[4] as List<Product>
        val countdown = array[5] as Long

        val hasWifi = conference.flags["enable_wifi"] ?: false
        val hasProducts = conference.flags["enable_merch"] ?: false
        // Find a random product with a media url to show off
        val productExample = if (hasProducts) {
            products.filter { it.media.isNotEmpty() }
                .randomOrNull()?.media?.first()?.url
        } else {
            null
        }
        HomeState.Loaded(
            forceTimeZone = storage.forceTimeZone,
            conferences = conferences,
            conference = conference,
            hasWifi = hasWifi,
            hasProducts = hasProducts,
            productExample = productExample,
            documents = documents,
            news = news,
            countdown = countdown,
        )
    }

    fun setConference(conference: Conference) {
        _countdown.value = -1L
        userSession.setConference(conference)
    }

    suspend fun setCountdown(remainder: Long) {
        _countdown.emit(remainder)
    }
}
