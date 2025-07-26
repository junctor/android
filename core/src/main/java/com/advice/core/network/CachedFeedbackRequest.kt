package com.advice.core.network

import com.advice.core.local.feedback.FeedbackForm

data class CachedFeedbackRequest(
    val contentId: Long,
    val feedbackForm: FeedbackForm,
)
