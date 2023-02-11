package com.advice.schedule.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.local.Article
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.schedule.utilities.Analytics
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val database by inject<DatabaseManager>()
    private val analytics by inject<Analytics>()

    private val conference: LiveData<Conference> = database.conference
    private val conferences: LiveData<List<Conference>> = database.conferences
    private val articles: LiveData<List<Article>>

    private val state: LiveData<HomeState>

    init {
        articles = Transformations.switchMap(database.conference) {
            val result = MediatorLiveData<List<Article>>()

            if (it == null) {
                result.value = emptyList()
            } else {
                result.addSource(database.getArticles(it)) {
                    result.value = it
                }
            }

            return@switchMap result
        }

        state = Transformations.switchMap(database.conference) { conference ->
            val result = MediatorLiveData<HomeState>()

            if (conference == null) {
                result.value = HomeState.Loading
            } else {
                result.addSource(articles) { articles ->
                    result.value = HomeState.Loaded(conferences.value ?: emptyList(), conference, articles)
                }
                result.addSource(conferences) { conferences ->
                    result.value = HomeState.Loaded(conferences, conference, articles.value ?: emptyList())
                }
            }

            return@switchMap result
        }
    }

    fun changeConference(conference: Conference) {
        analytics.onConferenceChangeEvent(conference)
        database.conference.value = null
        database.changeConference(conference.id)
    }

    fun getHomeState(): LiveData<HomeState> = state
}