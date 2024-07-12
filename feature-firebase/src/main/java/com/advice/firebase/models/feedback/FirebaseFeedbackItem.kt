package com.advice.firebase.models.feedback

data class FirebaseFeedbackItem(
    val caption_text: String = "",
    val id: Long = -1,
    val options: List<FirebaseFeedbackItemOption> = emptyList(),
    val select_maximum: Int = -1,
    val select_minimum: Int = -1,
    val select_orientation: String = "",
    val sort_order: Int = -1,
    val text_max_length: Int? = null,
    val type: String = "",
)
