package com.advice.firebase.models

import com.google.firebase.firestore.PropertyName

data class FirebaseTagType(
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("category")
    @set:PropertyName("category")
    var category: String = "",
    @get:PropertyName("is_browsable")
    @set:PropertyName("is_browsable")
    var isBrowsable: Boolean = true,
    @get:PropertyName("is_single_valued")
    @set:PropertyName("is_single_valued")
    var isSingleValued: Boolean = false,
    @get:PropertyName("label")
    @set:PropertyName("label")
    var label: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = 0,
    @get:PropertyName("tags")
    @set:PropertyName("tags")
    var tags: List<FirebaseTag> = emptyList(),
)
