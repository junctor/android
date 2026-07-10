package com.advice.firebase.models

import com.google.firebase.Timestamp
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
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp? = null,
    @get:PropertyName("updated_tsz")
    @set:PropertyName("updated_tsz")
    var updatedTsz: String? = null,
    @get:PropertyName("updated_at_str")
    @set:PropertyName("updated_at_str")
    var updatedAtStr: String? = null,
)
