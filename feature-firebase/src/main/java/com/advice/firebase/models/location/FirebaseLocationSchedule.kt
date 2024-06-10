package com.advice.firebase.models.location

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseLocationSchedule(
    val begin: String = "",
    val end: String = "",
    val notes: String? = null,
    val status: String = "closed",
) : Parcelable
