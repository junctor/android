package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.NewsArticle
import com.advice.core.ui.HomeState
import com.advice.play.AppManager
import com.advice.schedule.data.repositories.HomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository by inject<HomeRepository>()
    private val analytics by inject<AnalyticsProvider>()
    private val appManager by inject<AppManager>()

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)

    private var countdownJob: Job? = null

    init {
        viewModelScope.launch {
            repository.contents.collect {
                when (it) {
                    is HomeState.Error -> {
                        // no-op
                    }

                    is HomeState.Loaded -> {
                        // Check if there is any updates available
                        val isUpdateAvailable = appManager.isUpdateAvailable()
                        _state.value = it.copy(
                            isUpdateAvailable = isUpdateAvailable,
                            wifi = if(analytics.isWifiEnabled()) it.wifi else emptyList(),
                        )
                        if (countdownJob == null) {
                            startCountdown(it.conference)
                        }
                    }

                    HomeState.Loading -> {
                        // no-op
                    }
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
                    val value = _state.value as? HomeState.Loaded
                    if (value != null) {
                        _state.value = value.copy(countdown = remainder)
                    }
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

    fun getHomeState(): Flow<HomeState> = _state

    fun markLatestNewsAsRead(newsArticle: NewsArticle) {
        viewModelScope.launch {
            val temp = _state.value
            if (temp is HomeState.Loaded) {
                _state.value = temp.copy(news = null)
            }
            repository.markLatestNewsAsRead(newsArticle)
        }
    }

    companion object {
        private const val COUNTDOWN_DELAY = 250L
    }
}
