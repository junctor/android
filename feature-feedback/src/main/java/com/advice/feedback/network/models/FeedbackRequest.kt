package com.advice.feedback.network.models

data class FeedbackRequest(
    val feedbackFormId: Long,
    val contentId: Long,
    val conferenceId: Long,
    val client: String,
    val deviceId: String,
    val timestamp: String,
    val items: List<Feedback>,
)
