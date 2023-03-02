package com.advice.schedule.ui.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.utils.TimeUtil
import com.advice.schedule.database.repository.ScheduleRepository
import com.advice.schedule.models.local.Event
import com.advice.ui.screens.ScheduleScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ScheduleRepository>()

    private val query = MutableStateFlow<String?>(null)

    val state = combine(
        repository.list, query
    ) { events, query ->
        val elements = events.filter { query == null || it.title.contains(query, ignoreCase = true) }
        val days = elements.groupBy { TimeUtil.getDateStamp(it.start.toDate()) }
        return@combine ScheduleScreenState.Success(days)
    }

    fun bookmark(event: Event) {
        viewModelScope.launch {
            repository.bookmark(event)
        }
    }

    fun search(q: String) {
        viewModelScope.launch {
            query.emit(q)
        }
    }
}