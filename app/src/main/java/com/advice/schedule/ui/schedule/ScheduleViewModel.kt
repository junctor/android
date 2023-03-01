package com.advice.schedule.ui.schedule

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.utils.TimeUtil
import com.advice.schedule.database.repository.ScheduleRepository
import com.advice.schedule.models.local.Event
import com.advice.ui.screens.ScheduleScreenState
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ScheduleRepository>()

    private val state = MutableLiveData<ScheduleScreenState>(ScheduleScreenState.Init)


    init {
        viewModelScope.launch {
            repository.list.collect { elements ->
                val days = elements.groupBy { TimeUtil.getDateStamp(it.start.toDate()) }
                state.value = ScheduleScreenState.Success(days)
            }
        }
    }

    fun getState(): LiveData<ScheduleScreenState> = state

    fun bookmark(event: Event) {
        viewModelScope.launch {
            repository.bookmark(event)
        }
    }
}