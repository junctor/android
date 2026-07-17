package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.ui.states.EventScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class EventViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ScheduleRepository>()
    private val contentRepository by inject<ContentRepository>()

    private val _state = MutableStateFlow<EventScreenState>(EventScreenState.Loading)
    val state: StateFlow<EventScreenState> = _state

    fun getEvent(conference: String?, id: Long?, session: Long?) {
        viewModelScope.launch {
            val value = _state.value
            if (value !is EventScreenState.Success ||
                value.content.conference != conference ||
                value.content.id != id ||
                value.session?.id != session
            ) {
                _state.value = EventScreenState.Loading
            }
            if (conference == null || id == null) {
                Timber.e("Could not find Event: conference or id is null")
                _state.value = EventScreenState.Error("Invalid event id")
                return@launch
            }

            loadEvent(conference, id, session)
        }
    }

    private suspend fun loadEvent(
        conference: String,
        contentId: Long,
        sessionId: Long?,
    ) {
        val content = contentRepository.getContent(conference, contentId)

        if (content == null) {
            Timber.e("Content not found - showing error state")
            _state.value = EventScreenState.Error("Content not found")
            return
        }

        val relatedContent = content.relatedContentIds.mapNotNull {
            contentRepository.getContent(conference, it)
        }

        _state.value =
            EventScreenState.Success(
                content = content,
                session = content.sessions.find { it.id == sessionId },
                relatedContent = relatedContent,
            )
    }

    private suspend fun refreshEvent() {
        val event = _state.value as? EventScreenState.Success ?: return
        loadEvent(event.content.conference, event.content.id, event.session?.id)
    }

    fun bookmark(content: Content, session: Session?, isBookmarked: Boolean) {
        viewModelScope.launch {
            repository.bookmark(content, session, isBookmarked)
            refreshEvent()
        }
    }
}
