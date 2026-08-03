package com.advice.firebase.models.feedback

import com.google.firebase.firestore.PropertyName

data class FirebaseFeedbackItemOption(
    @get:PropertyName("caption_text")
    @set:PropertyName("caption_text")
    var captionText: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
)
