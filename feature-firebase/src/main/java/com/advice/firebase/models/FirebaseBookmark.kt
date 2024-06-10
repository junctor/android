package com.advice.firebase.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseBookmark(
    val id: String = "",
    val value: Boolean = false,
) : Parcelable
