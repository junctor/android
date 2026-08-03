package com.advice.firebase.models.feedback

import com.google.firebase.firestore.PropertyName

data class FirebaseFeedbackForm(
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1,
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("items")
    @set:PropertyName("items")
    var items: List<FirebaseFeedbackItem> = emptyList(),
    @get:PropertyName("name_text")
    @set:PropertyName("name_text")
    var nameText: String = "",
    @get:PropertyName("submission_url")
    @set:PropertyName("submission_url")
    var submissionUrl: String = "",
)
