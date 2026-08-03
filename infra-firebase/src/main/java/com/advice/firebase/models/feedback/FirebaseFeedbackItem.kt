package com.advice.firebase.models.feedback

import com.google.firebase.firestore.PropertyName

data class FirebaseFeedbackItem(
    @get:PropertyName("caption_text")
    @set:PropertyName("caption_text")
    var captionText: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("options")
    @set:PropertyName("options")
    var options: List<FirebaseFeedbackItemOption> = emptyList(),
    @get:PropertyName("select_maximum")
    @set:PropertyName("select_maximum")
    var selectMaximum: Int = -1,
    @get:PropertyName("select_minimum")
    @set:PropertyName("select_minimum")
    var selectMinimum: Int = -1,
    @get:PropertyName("select_orientation")
    @set:PropertyName("select_orientation")
    var selectOrientation: String = "",
    @get:PropertyName("sort_order")
    @set:PropertyName("sort_order")
    var sortOrder: Int = -1,
    @get:PropertyName("text_max_length")
    @set:PropertyName("text_max_length")
    var textMaxLength: Int? = null,
    @get:PropertyName("type")
    @set:PropertyName("type")
    var type: String = "",
)
