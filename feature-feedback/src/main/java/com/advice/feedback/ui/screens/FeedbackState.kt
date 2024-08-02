package com.advice.feedback.ui.screens

import com.advice.core.local.feedback.FeedbackForm

sealed class FeedbackState {
    data object Loading : FeedbackState()
    data class Success(
        val feedback: FeedbackForm,
        val showingDiscardPopup: Boolean = false,
        val isLoading: Boolean = false
    ) : FeedbackState()

    data object Error : FeedbackState()
}
