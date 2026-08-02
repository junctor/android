package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.network.NetworkResponse
import com.advice.core.utils.ToastData
import com.advice.core.utils.ToastManager
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.ui.screens.FeedbackState
import com.advice.schedule.data.repositories.FeedbackFormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class FeedbackViewModel :
    ViewModel(),
    KoinComponent {
    private val formRepository by inject<FeedbackFormRepository>()
    private val submissionRepository by inject<FeedbackSubmissionRepository>()
    private val toastManager by inject<ToastManager>()

    private val _state = MutableStateFlow<FeedbackState>(FeedbackState.Loading)
    val state: Flow<FeedbackState> = _state

    fun fetchFeedbackForm(id: Long) {
        _state.value = FeedbackState.Loading
        viewModelScope.launch {
            val form = formRepository.getFeedbackForm(id)
            if (form != null) {
                _state.value = FeedbackState.Content(form)
            } else {
                _state.value = FeedbackState.Error(Exception("Could not load feedback form"))
            }
        }
    }

    fun onValueChanged(
        item: FeedbackItem,
        value: String,
    ) {
        val state = _state.value as? FeedbackState.Content ?: return
        val items = applyFeedbackValueChange(state.feedback.items, item, value)
        _state.value = FeedbackState.Content(state.feedback.copy(items = items))
    }

    fun submitFeedback(content: Long?) {
        val state = _state.value as? FeedbackState.Content ?: return
        _state.value = state.copy(isLoading = true)

        viewModelScope.launch {
            when (val response = submissionRepository.submitFeedback(content, state.feedback)) {
                NetworkResponse.Success -> {
                    _state.value =
                        state.copy(
                            isLoading = false,
                            isComplete = true,
                        )
                }

                is NetworkResponse.Error -> {
                    val message = response.exception.message
                    val text = "Could not submit feedback: " + (message ?: "unknown error")

                    _state.value =
                        state.copy(
                            isLoading = false,
                            isComplete = true,
                            errorMessage = text,
                        )

                    toastManager.push(ToastData(text))
                }
            }
        }
    }

    fun onBackPressed() {
        val state = _state.value as? FeedbackState.Content ?: return
        _state.value = state.copy(showingDiscardPopup = true)
    }

    fun onDiscardPopupCancelled() {
        val state = _state.value as? FeedbackState.Content ?: return
        _state.value = state.copy(showingDiscardPopup = false)
    }
}
