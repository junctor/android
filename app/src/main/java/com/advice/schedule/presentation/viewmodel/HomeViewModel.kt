package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.Conference
import com.advice.core.local.NewsArticle
import com.advice.core.ui.HomeState
import com.advice.core.utils.NotificationHelper
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.play.AppManager
import com.advice.schedule.data.repositories.HomeRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Date
import kotlin.time.Duration.Companion.milliseconds

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository by inject<HomeRepository>()
    private val analytics by inject<AnalyticsProvider>()
    private val appManager by inject<AppManager>()
    private val notificationHelper by inject<NotificationHelper>()
    private val documentRepository by inject<DocumentsRepository>()

    private val _state = MutableStateFlow<HomeState>(HomeState.Loading)
    private val _countdown = MutableStateFlow(0L)

    private var countdownJob: Job? = null
    private var emergencyDocumentId: Long? = null

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
                        )
                        if (countdownJob == null) {
                            startCountdown(it.conference)
                        }

                        // Showing notification in emergency document is present
                        val id = it.conference.emergencyDocumentId
                        if (id != emergencyDocumentId) {
                            emergencyDocumentId = id
                            if (id != null) {
                                val document = documentRepository.get(id)
                                if (document != null) {
                                    notificationHelper.notifyEmergency(document)
                                }
                            }
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
                    _countdown.value = remainder.coerceAtLeast(0L)
                    delay(COUNTDOWN_DELAY.milliseconds)
                }
                _countdown.value = 0L
            }
        } else {
            _countdown.value = 0L
        }
    }

    fun setConference(conference: Conference) {
        countdownJob?.cancel()
        countdownJob = null
        _countdown.value = 0L
        viewModelScope.launch {
            repository.setConference(conference)
        }
        analytics.onConferenceChangeEvent(conference)
    }

    fun getHomeState(): Flow<HomeState> = _state

    fun getCountdown(): StateFlow<Long> = _countdown.asStateFlow()

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
