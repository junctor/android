package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Content
import com.advice.core.local.FlowResult
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.domain.ContentBookmarkUseCase
import com.advice.ui.states.ContentScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ContentViewModel(
    private val repository: ContentRepository,
    private val contentBookmarkUseCase: ContentBookmarkUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow<ContentScreenState>(ContentScreenState.Loading)
    val state: Flow<ContentScreenState> = _state

    init {
        viewModelScope.launch {
            repository.content.collect { result ->
                when (result) {
                    FlowResult.Loading -> _state.value = ContentScreenState.Loading
                    is FlowResult.Failure ->
                        _state.value = ContentScreenState.Error
                    is FlowResult.Success ->
                        _state.value = ContentScreenState.Success(result.value.content)
                }
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
