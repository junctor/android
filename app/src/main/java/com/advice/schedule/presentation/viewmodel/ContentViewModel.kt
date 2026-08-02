package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Content
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.domain.ContentBookmarkUseCase
import com.advice.ui.states.ContentScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ContentViewModel :
    ViewModel(),
    KoinComponent {
    private val repository by inject<ContentRepository>()
    private val contentBookmarkUseCase by inject<ContentBookmarkUseCase>()

    private val _state = MutableStateFlow<ContentScreenState>(ContentScreenState.Loading)
    val state: Flow<ContentScreenState> = _state

    init {
        viewModelScope.launch {
            repository.content.collect {
                _state.value = ContentScreenState.Success(it.content)
            }
        }
    }

    fun bookmark(
        content: Content,
        isBookmarked: Boolean,
    ) {
        viewModelScope.launch {
            contentBookmarkUseCase.bookmark(content, session = null, isBookmarked = isBookmarked)
        }
    }
}
