package com.advice.firebase.models

data class FirebaseDocument(
    val conference_id: Long = -1L,
    val conference: String = "",
    val id: Long = -1L,
    val title_text: String = "",
    val body_text: String = "",
)
