package com.advice.feedback.ui.screens

import com.advice.core.local.feedback.FeedbackForm

sealed class FeedbackState {
    object Loading : FeedbackState()
    data class Success(val feedback: FeedbackForm) : FeedbackState()
    object Error : FeedbackState()
}
