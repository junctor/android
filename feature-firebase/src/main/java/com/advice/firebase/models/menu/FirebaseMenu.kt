package com.advice.firebase.models.menu

data class FirebaseMenu(
    val conference: String = "",
    val conference_id: Long = -1,
    val id: Long = -1,
    val title_text: String = "",
    val items: List<FirebaseMenuItem> = emptyList(),
)
