package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationRow(
    val id: Long,
    val title: String,
    val status: LocationStatus,
    val depth: Int,
    val hasChildren: Boolean,
    val isExpanded: Boolean,
    val schedule: List<LocationSchedule>,
) : Parcelable