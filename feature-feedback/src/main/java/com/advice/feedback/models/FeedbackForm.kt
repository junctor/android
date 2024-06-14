package com.advice.feedback.models

data class FeedbackForm(
    val id: Long,
    val items: List<FeedbackItem>,
)