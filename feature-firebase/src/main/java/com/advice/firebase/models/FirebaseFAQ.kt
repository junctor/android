package com.advice.firebase.models

import com.google.firebase.firestore.PropertyName

data class FirebaseFAQ(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Int = -1,
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("question")
    @set:PropertyName("question")
    var question: String = "",
    @get:PropertyName("answer")
    @set:PropertyName("answer")
    var answer: String = "",
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: String? = null,
)
