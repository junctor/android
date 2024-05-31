package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Session(
    val channelId: Long?,
    val locationId: Long,
    val recordingPolicyId: Long?,
    val sessionId: Long,
    val beginTime: Instant,
    val endTime: Instant,
    val timezoneName: String,
) : Parcelable
