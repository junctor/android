package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Session
import com.advice.core.ui.ScheduleFilter
import com.advice.core.utils.Storage
import com.advice.core.utils.TimeUtil
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.ScheduleResult
import com.advice.ui.states.EventScreenState
import com.advice.ui.states.ScheduleScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ScheduleViewModel : ViewModel(), KoinComponent {

    private val storage by inject<Storage>()
    private val repository by inject<ScheduleRepository>()
    private val contentRepository by inject<ContentRepository>()

    private val _state = MutableStateFlow<EventScreenState>(EventScreenState.Loading)
    val state: StateFlow<EventScreenState> = _state

    fun getEvent(conference: String?, id: Long?, session: Long?) {
        viewModelScope.launch {
            _state.value = EventScreenState.Loading
            if (conference == null || id == null) {
                Timber.e("Could not find Event: conference or id is null")
                _state.value = EventScreenState.Error("Invalid event id")
                return@launch
            }

            getEvent(conference, id, session)
        }
    }

    private suspend fun getEvent(
        conference: String,
        contentId: Long,
        sessionId: Long?
    ) {
        val content = contentRepository.getContent(conference, contentId)

        if (content == null) {
            Timber.e("Content not found - showing error state")
            _state.value = EventScreenState.Error("Content not found")
            return
        }

        // Find the session, if session is not null.
        _state.value =
            EventScreenState.Success(content, content.sessions.find { it.id == sessionId })
    }

    private suspend fun refreshEvent() {
        val event = _state.value as? EventScreenState.Success ?: return
        getEvent(event.content.conference, event.content.id, event.session?.id)
    }

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

    fun bookmark(content: Content, session: Session?, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmark(content, session, isBookmarked)
            refreshEvent()
        }
    }

    fun bookmark(event: Event, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmark(event.content, event.session, isBookmarked)
        }
    }
}
