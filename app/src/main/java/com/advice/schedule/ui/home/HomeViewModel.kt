package com.advice.schedule.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.schedule.repository.HomeRepository
import com.advice.schedule.utilities.Analytics
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.Date

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository by inject<HomeRepository>()
    private val analytics by inject<Analytics>()

    private val state = MutableLiveData<HomeState>()

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
        var remainder = conference.startDate.time - Date().time
        if (remainder > 0L) {
            countdownJob = viewModelScope.launch {
                while (remainder > 0L) {
                    remainder = conference.kickoffDate.time - Date().time
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

    fun getHomeState(): LiveData<HomeState> = state

    companion object {
        private const val COUNTDOWN_DELAY = 250L
    }
}