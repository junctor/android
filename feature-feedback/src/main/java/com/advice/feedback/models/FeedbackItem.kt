package com.advice.feedback.models

data class FeedbackItem(
    val id: Long,
    val caption: String,
    // sort_order
    val type: FeedbackType,
)