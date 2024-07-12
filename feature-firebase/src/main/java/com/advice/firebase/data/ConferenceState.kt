package com.advice.firebase.data

import com.advice.core.local.Conference
import com.advice.core.local.Location
import com.advice.core.local.Speaker
import com.advice.core.local.TagType
import com.advice.core.local.feedback.FeedbackForm

data class ConferenceState(
    val conference: Conference,
    val tags: List<TagType>,
    val speakers: List<Speaker>,
    val locations: List<Location>,
    val feedbackforms: List<FeedbackForm>,
)
