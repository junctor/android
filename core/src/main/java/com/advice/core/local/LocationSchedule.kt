package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationSchedule(
    val begin: String = "",
    val end: String = "",
    val notes: String? = null,
    val status: String = "closed"
) : Parcelable
