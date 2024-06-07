package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceContent(
    val events: List<Event>,
    val content: List<Content>,
) : Parcelable
