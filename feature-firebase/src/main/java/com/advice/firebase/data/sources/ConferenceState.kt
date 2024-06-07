package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.Location
import com.advice.core.local.Speaker
import com.advice.core.local.TagType

data class ConferenceState(
    val conference: Conference,
    val tags: List<TagType>,
    val speakers: List<Speaker>,
    val locations: List<Location>,
)