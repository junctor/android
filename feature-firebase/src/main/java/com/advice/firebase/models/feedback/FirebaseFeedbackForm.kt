package com.advice.firebase.models.feedback

data class FirebaseFeedbackForm(
    val conference_id: Long = -1,
    val id: Long = -1,
    val items: List<FirebaseFeedbackItem> = emptyList(),
    val name_text: String = "",
)
