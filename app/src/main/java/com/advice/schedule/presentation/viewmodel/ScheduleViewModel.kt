package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Event
import com.advice.core.ui.ScheduleFilter
import com.advice.core.utils.Storage
import com.advice.core.utils.TimeUtil
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.ui.states.EventScreenState
import com.advice.ui.states.ScheduleScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val storage by inject<Storage>()
    private val repository by inject<ScheduleRepository>()
    private val contentRepository by inject<ContentRepository>()

    fun getEvent(conference: String?, id: Long?, session: Long?): Flow<EventScreenState> {
        return flow {
            emit(EventScreenState.Loading)

            Timber.i("Getting content $id with session $session")

            if (conference == null || id == null) {
                Timber.e("Could not find Event: conference or id is null")
                emit(EventScreenState.Error("Invalid event id"))
                return@flow
            }

            val content = contentRepository.getContent(conference, id)


            if (content == null) {
                Timber.e("Content not found")
                emit(EventScreenState.Error("Content not found"))
                return@flow
            }

            // Find the session, if session is not null.
            val session = content.sessions.find { it.id == session }

            emit(EventScreenState.Success(content, session))
        }
    }

    fun getState(filter: ScheduleFilter = ScheduleFilter.Default): Flow<ScheduleScreenState> {
        return repository.getSchedule(filter).map { elements ->
            val days = elements.groupBy { TimeUtil.getDateStamp(it.session, storage.forceTimeZone) }
            return@map ScheduleScreenState.Success(filter, days)
        }
    }

    fun bookmark(event: Event, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmark(event, isBookmarked)
        }
    }
}
