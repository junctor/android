package com.advice.firebase.models

import com.google.firebase.firestore.PropertyName

data class FirebaseDocument(
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1L,
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1L,
    @get:PropertyName("title_text")
    @set:PropertyName("title_text")
    var titleText: String = "",
    @get:PropertyName("body_text")
    @set:PropertyName("body_text")
    var bodyText: String = "",
)
