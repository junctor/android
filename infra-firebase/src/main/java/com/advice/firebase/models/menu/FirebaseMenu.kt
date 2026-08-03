package com.advice.firebase.models.menu

import com.google.firebase.firestore.PropertyName

data class FirebaseMenu(
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("title_text")
    @set:PropertyName("title_text")
    var titleText: String = "",
    @get:PropertyName("items")
    @set:PropertyName("items")
    var items: List<FirebaseMenuItem> = emptyList(),
)
