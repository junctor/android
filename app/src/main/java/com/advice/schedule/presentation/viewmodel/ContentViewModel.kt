package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Content
import com.advice.reminder.ReminderManager
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.ui.states.ContentScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ContentViewModel :
    ViewModel(),
    KoinComponent {
    private val repository by inject<ContentRepository>()
    private val reminderManager by inject<ReminderManager>()

    private val _state = MutableStateFlow<ContentScreenState>(ContentScreenState.Loading)
    val state: Flow<ContentScreenState> = _state

    init {
        viewModelScope.launch {
            repository.content.collect {
                _state.value = ContentScreenState.Success(it.content)
            }
        }
    }

    fun bookmark(content: Content, isBookmarked: Boolean) {
        viewModelScope.launch {
            // Bookmarking content that has sessions
            if (content.sessions.isNotEmpty()) {
                val all = content.sessions.all { repository.isBookmarked(it) }
                val any = content.sessions.any { repository.isBookmarked(it) }

                when {
                    !isBookmarked && all -> {
                        Timber.d("All sessions are bookmarked - unbookmarking all")
                        // All sessions are bookmarked - unbookmark them all
                        content.sessions.forEach {
                            repository.bookmark(content, it)
                            reminderManager.removeReminder(content, it)
                        }
                    }

                    isBookmarked && !any -> {
                        Timber.d("No sessions are bookmarked - bookmarking all")
                        // No sessions are bookmarked - bookmark them all
                        content.sessions.forEach {
                            repository.bookmark(content, it)
                            reminderManager.setReminder(content, it)
                        }
                    }
                }
            }

            repository.bookmark(content)
        }
    }
}
