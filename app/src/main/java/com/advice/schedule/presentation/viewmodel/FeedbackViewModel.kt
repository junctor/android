package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackType
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.data.repositories.FeedbackRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeedbackViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FeedbackRepository>()
    private val feedbackRepository by inject<com.advice.feedback.network.FeedbackRepository>()

    private val _state = MutableStateFlow<FeedbackState>(FeedbackState.Loading)
    val state: Flow<FeedbackState> = _state

    fun fetchFeedbackForm(id: Long) {
        _state.value = FeedbackState.Loading
        viewModelScope.launch {
            val form = repository.getFeedbackForm(id)
            if (form != null) {
                _state.value = FeedbackState.Success(form)
            } else {
                _state.value = FeedbackState.Error
            }
        }
    }

    fun onValueChanged(item: FeedbackItem, value: String) {
        val state = _state.value as? FeedbackState.Success ?: return

        val items = state.feedback.items.map {
            if (it.id == item.id) {
                when (val type = it.type) {
                    FeedbackType.DisplayOnly -> it
                    is FeedbackType.MultiSelect -> {
                        val selections = if (value.toLong() in type.selections) {
                            type.selections.filter { it != value.toLong() }
                        } else {
                            type.selections + value.toLong()
                        }
                        it.copy(type = FeedbackType.MultiSelect(type.options, selections))
                    }

                    is FeedbackType.SelectOne -> {
                        it.copy(type = FeedbackType.SelectOne(type.options, value.toLong()))
                    }

                    is FeedbackType.TextBox -> {
                        it.copy(type = FeedbackType.TextBox(value))
                    }
                }
            } else {
                it
            }
        }

        _state.value = FeedbackState.Success(state.feedback.copy(items = items))
    }

    fun submitFeedback(content: Long) {
        val state = _state.value as? FeedbackState.Success ?: return
        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            feedbackRepository.submitFeedback(content, state.feedback)
        }
    }

    fun onBackPressed() {
        val state = _state.value as? FeedbackState.Success ?: return
        _state.value = state.copy(showingDiscardPopup = true)
    }

    fun onDiscardPopupCancelled() {
        val state = _state.value as? FeedbackState.Success ?: return
        _state.value = state.copy(showingDiscardPopup = false)
    }
}
