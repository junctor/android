package com.advice.feedback.models

sealed class FeedbackType {
    object DisplayOnly : FeedbackType()

    data class SelectOne(val options: List<String>) : FeedbackType()

    data class MultiSelect(val options: List<String>) : FeedbackType()

    data class TextBox(val value: String) : FeedbackType()
}