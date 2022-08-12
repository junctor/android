package com.advice.schedule.ui.home

import androidx.lifecycle.*
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.models.firebase.FirebaseUser
import com.advice.schedule.models.local.Article
import com.advice.schedule.models.local.Conference
import com.advice.schedule.utilities.Analytics
import com.advice.schedule.utilities.Storage
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val preferences by inject<Storage>()
    private val database by inject<DatabaseManager>()
    private val analytics by inject<Analytics>()

    private val conference: LiveData<Conference> = database.conference
    private val conferences: LiveData<List<Conference>> = database.conferences
    private val articles: LiveData<List<Article>>
    private var user = MutableLiveData<FirebaseUser?>()
    private val state: LiveData<HomeState>

    init {
        user.value = preferences.user

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
                    result.value = HomeState.Loaded(user.value, conferences.value ?: emptyList(), conference, articles)
                }
                result.addSource(conferences) { conferences ->
                    result.value = HomeState.Loaded(user.value, conferences, conference, articles.value ?: emptyList())
                }
                result.addSource(user) { user ->
                    result.value = HomeState.Loaded(user, conferences.value ?: emptyList(), conference, articles.value ?: emptyList())
                }
            }

            return@switchMap result
        }
    }

    fun onResume() {
        viewModelScope.launch {
            val temp = database.getUser()
            if (temp != null) {
                user.value = temp
            }
        }
    }

    fun changeConference(conference: Conference) {
        analytics.onConferenceChangeEvent(conference)
        database.conference.value = null
        database.changeConference(conference.id)
    }

    fun getHomeState(): LiveData<HomeState> = state
}

sealed class HomeState {
    object Loading : HomeState()
    data class Loaded(val user: FirebaseUser?, val conferences: List<Conference>, val conference: Conference, val article: List<Article>) : HomeState()
    data class Error(val ex: Exception) : HomeState()
}