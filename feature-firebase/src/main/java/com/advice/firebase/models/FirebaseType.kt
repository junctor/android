package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseType(
    val id: Long = -1,
    val name: String = "",
    val description: String = "",
    val conference: String = "",
    val color: String = "#343434",
    val discord_url: String? = null,
    val subforum_url: String? = null,
    val youtube_url: String? = null,
    val eventdescriptionfooter: String? = null,
    val updated_at: String? = null,
    val tags: String? = null,
) : Parcelable
