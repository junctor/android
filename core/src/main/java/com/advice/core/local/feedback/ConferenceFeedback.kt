package com.advice.core.local.feedback

const val GENERAL_FEEDBACK_TITLE = "General Feedback"

fun List<FeedbackForm>.generalFeedbackForm(): FeedbackForm? = find { it.title == GENERAL_FEEDBACK_TITLE }
