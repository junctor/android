package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ConferenceContent(
    val content: List<Content>,
) : Parcelable
