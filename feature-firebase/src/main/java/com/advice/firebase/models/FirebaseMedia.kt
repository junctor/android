package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseMedia(
    val asset_id: Long = -1L,
    val is_logo: String = "N",
    val name: String = "",
    val sort_order: Int = 0,
    val url: String = "",
) : Parcelable
