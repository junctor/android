package com.advice.feedback.network.models

data class Feedback(
    val itemId: Long,
    val options: List<Long> = emptyList(),
    val text: String = "",
)
