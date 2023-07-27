package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Event
import com.advice.core.ui.ScheduleFilter
import com.advice.core.utils.Storage
import com.advice.core.utils.TimeUtil
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.ui.screens.ScheduleScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.cancel
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber


class ScheduleViewModel : ViewModel(), KoinComponent {

    private val storage by inject<Storage>()
    private val repository by inject<ScheduleRepository>()

    fun getEvent(conference: String?, id: Long?): Flow<Event?> {
        return flow {
            if (conference == null || id == null) {
                Timber.e("Conference or id is null")
                emit(null)
                return@flow
            }

            val value = repository.getEvent(conference, id)
            emit(value)
        }
    }

    fun getState(filter: ScheduleFilter = ScheduleFilter.Default): Flow<ScheduleScreenState> {
        return repository.getSchedule(filter).map { elements ->
            val days = elements.groupBy { TimeUtil.getDateStamp(it, storage.forceTimeZone) }
            return@map ScheduleScreenState.Success(filter, days)
        }
    }

    fun bookmark(event: Event, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmark(event, isBookmarked)
        }
    }
}