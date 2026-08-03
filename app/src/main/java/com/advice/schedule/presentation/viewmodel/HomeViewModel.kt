package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.NewsArticle
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.play.AppManager
import com.advice.reminder.NotificationHelper
import com.advice.schedule.data.repositories.HomeRepository
import com.advice.ui.states.HomeState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel(
    private val repository: HomeRepository,
    private val analytics: AnalyticsProvider,
    private val appManager: AppManager,
    private val notificationHelper: NotificationHelper,
    private val documentRepository: DocumentsRepository,
) : ViewModel() {
    private val state = MutableStateFlow<HomeState>(HomeState.Loading)
    private val countdown = MutableStateFlow(0L)

    private var countdownJob: Job? = null
    private var emergencyDocumentId: Long? = null

    init {
        viewModelScope.launch {
            repository.contents.collect {
                when (it) {
                    is HomeState.Error -> {
                        state.value = it
                    }

                    is HomeState.Loaded -> {
                        // Check if there is any updates available
                        val isUpdateAvailable = appManager.isUpdateAvailable()
                        state.value =
                            it.copy(
                                isUpdateAvailable = isUpdateAvailable,
                            )
                        if (countdownJob == null) {
                            startCountdown(it.conference)
                        }

                        // Showing notification when emergency document is present
                        val id = it.conference.emergencyDocumentId
                        if (id != emergencyDocumentId) {
                            emergencyDocumentId = id
                            if (id != null) {
                                val document = documentRepository.get(id)
                                if (document != null) {
                                    notificationHelper.notifyEmergency(
                                        document,
                                        it.conference.code,
                                    )
                                }
                            }
                        }
                    }

                    HomeState.Loading -> {
                        state.value = HomeState.Loading
                    }
                }
            }
        }
    }

    private fun startCountdown(conference: Conference) {
        var remainder = conference.kickoffDate.toEpochMilli() - System.currentTimeMillis()
        if (remainder > 0L) {
            countdownJob =
                viewModelScope.launch {
                    while (remainder > 0L) {
                        remainder = conference.kickoffDate.toEpochMilli() - System.currentTimeMillis()
                        countdown.value = remainder.coerceAtLeast(0L)
                        delay(COUNTDOWN_DELAY.milliseconds)
                    }
                    countdown.value = 0L
                }
        } else {
            countdown.value = 0L
        }
    }

    fun setConference(conference: Conference) {
        countdownJob?.cancel()
        countdownJob = null
        countdown.value = 0L
        viewModelScope.launch {
            repository.setConference(conference)
        }
        analytics.onConferenceChangeEvent(conference)
    }

    fun retry() {
        state.value = HomeState.Loading
        repository.refreshConference()
    }

    fun getHomeState(): Flow<HomeState> = state

    fun getCountdown(): StateFlow<Long> = countdown.asStateFlow()

    fun markLatestNewsAsRead(newsArticle: NewsArticle) {
        viewModelScope.launch {
            val temp = state.value
            if (temp is HomeState.Loaded) {
                state.value = temp.copy(news = null)
            }
            repository.markLatestNewsAsRead(newsArticle)
        }
    }

    companion object {
        private const val COUNTDOWN_DELAY = 250L
    }
}
