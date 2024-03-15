package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class LocationSchedule(
    val start: Instant,
    val end: Instant,
    val notes: String?,
    val status: String,
) : Parcelable