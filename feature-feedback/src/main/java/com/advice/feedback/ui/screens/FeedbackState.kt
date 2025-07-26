package com.advice.feedback.ui.screens

import com.advice.core.local.feedback.FeedbackForm

sealed class FeedbackState {
    data object Loading : FeedbackState()

    data class Content(
        val feedback: FeedbackForm,
        val showingDiscardPopup: Boolean = false,
        val isLoading: Boolean = false,
        val isComplete: Boolean = false,
        val errorMessage: String? = null,
    ) : FeedbackState()

    data class Error(
        val exception: Exception,
    ) : FeedbackState()
}
