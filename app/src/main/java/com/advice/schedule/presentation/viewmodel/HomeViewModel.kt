package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.NewsArticle
import com.advice.core.ui.HomeState
import com.advice.schedule.data.repositories.HomeRepository
import java.util.Date
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository by inject<HomeRepository>()
    private val analytics by inject<AnalyticsProvider>()

    private val state = MutableStateFlow<HomeState>(HomeState.Loading)

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            repository.contents.collect {
                state.value = it

                if (countdownJob == null) {
                    startCountdown(it.conference)
                }
            }
        }
    }

    private fun startCountdown(conference: Conference) {
        var remainder = conference.kickoffDate.toEpochMilli() - Date().time
        if (remainder > 0L) {
            countdownJob = viewModelScope.launch {
                while (remainder > 0L) {
                    remainder = conference.kickoffDate.toEpochMilli() - Date().time
                    repository.setCountdown(remainder)
                    delay(COUNTDOWN_DELAY)
                }
            }
        }
    }

    fun setConference(conference: Conference) {
        countdownJob?.cancel()
        countdownJob = null
        viewModelScope.launch {
            repository.setConference(conference)
        }
        analytics.onConferenceChangeEvent(conference)
    }

    fun getHomeState(): Flow<HomeState> = state

    fun markLatestNewsAsRead(newsArticle: NewsArticle) {
        viewModelScope.launch {
            repository.markLatestNewsAsRead(newsArticle)
        }
    }

    companion object {
        private const val COUNTDOWN_DELAY = 250L
    }
}
