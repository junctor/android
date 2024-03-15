package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeakerLink(
    val title: String = "",
    val description: String = "",
    val url: String = "",
    val sort_order: Int = 0,
) : Parcelable
