package com.advice.schedule.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Conference
import com.advice.core.ui.HomeState
import com.advice.schedule.database.repository.HomeRepository
import com.advice.schedule.utilities.Analytics
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class HomeViewModel : ViewModel(), KoinComponent {

    private val repository by inject<HomeRepository>()

    private val analytics by inject<Analytics>()

    private val state = MutableLiveData<HomeState>()

    init {
        viewModelScope.launch {
            repository.contents.collect {
                state.value = it
            }
        }
    }

    fun setConference(conference: Conference) {
        viewModelScope.launch {
            repository.setConference(conference)
        }
        analytics.onConferenceChangeEvent(conference)
    }

    fun getHomeState(): LiveData<HomeState> = state
}