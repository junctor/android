package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSpeakerWithEvents(
    val id: Int = -1,
    val name: String = "",
    val description: String = "",
    val link: String = "",
    val twitter: String = "",
    val title: String = "",

    val conference: String? = null,
    val updated_at: String? = null,
    val events: List<FirebaseEvent>? = null,

    val hidden: Boolean = false
) : Parcelable
