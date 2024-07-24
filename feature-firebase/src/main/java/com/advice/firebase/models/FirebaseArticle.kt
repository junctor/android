package com.advice.firebase.models

import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

data class FirebaseArticle(
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1,
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String? = null,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Int = -1,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("text")
    @set:PropertyName("text")
    var text: String = "",
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: Timestamp? = null,
    @get:PropertyName("updated_tsz")
    @set:PropertyName("updated_tsz")
    var updatedTsz: String? = null,
    @get:PropertyName("hidden")
    @set:PropertyName("hidden")
    var hidden: Boolean = false,
)
