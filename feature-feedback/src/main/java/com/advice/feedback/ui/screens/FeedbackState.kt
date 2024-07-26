package com.advice.feedback.ui.screens

import com.advice.core.local.feedback.FeedbackForm

sealed class FeedbackState {
    data object Loading : FeedbackState()
    data class Success(val feedback: FeedbackForm) : FeedbackState()
    data object Error : FeedbackState()
}
