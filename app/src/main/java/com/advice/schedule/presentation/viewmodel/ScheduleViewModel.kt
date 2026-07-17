package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Event
import com.advice.core.ui.ScheduleFilter
import com.advice.core.utils.Storage
import com.advice.core.utils.TimeUtil
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.ScheduleResult
import com.advice.ui.states.ScheduleScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val storage by inject<Storage>()
    private val repository by inject<ScheduleRepository>()

    fun getState(filter: ScheduleFilter = ScheduleFilter.Default): Flow<ScheduleScreenState> {
        return repository.getSchedule(filter).map { result ->
            when (result) {
                ScheduleResult.Loading -> {
                    ScheduleScreenState.Loading
                }

                is ScheduleResult.Empty -> {
                    ScheduleScreenState.Empty(result.message)
                }

                is ScheduleResult.Success -> {
                    val days = result.events.groupBy {
                        TimeUtil.getDateStamp(
                            it.session,
                            storage.forceTimeZone
                        )
                    }
                    ScheduleScreenState.Success(filter, days, storage.showFilters)
                }
            }
        }
    }

    fun bookmark(event: Event, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmark(event.content, event.session, isBookmarked)
        }
    }
}
