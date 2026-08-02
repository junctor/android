package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Event
import com.advice.core.storage.UserPreferencesStore
import com.advice.core.ui.ScheduleFilter
import com.advice.core.utils.TimeUtil
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.ScheduleResult
import com.advice.ui.states.ScheduleScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ScheduleViewModel :
    ViewModel(),
    KoinComponent {
    private val storage by inject<UserPreferencesStore>()
    private val repository by inject<ScheduleRepository>()

    fun getState(filter: ScheduleFilter = ScheduleFilter.Default): Flow<ScheduleScreenState> =
        combine(
            repository.getSchedule(filter),
            storage.scheduleDayFormatFlow,
        ) { result, dayFormat ->
            when (result) {
                ScheduleResult.Loading -> {
                    ScheduleScreenState.Loading
                }

                is ScheduleResult.Empty -> {
                    ScheduleScreenState.Empty(result.message)
                }

                is ScheduleResult.Success -> {
                    val days =
                        result.events.groupBy {
                            TimeUtil.getDateStamp(
                                it.session,
                                storage.forceTimeZone,
                                dayFormat,
                            )
                        }
                    ScheduleScreenState.Success(filter, days, storage.showFilters)
                }
            }
        }

    fun bookmark(
        event: Event,
        isBookmarked: Boolean,
    ) {
        viewModelScope.launch {
            repository.bookmark(event.content, event.session, isBookmarked)
        }
    }
}
