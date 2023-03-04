package com.advice.core.local

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Location(
    val id: Long,
    val name: String,
    val shortName: String?,
    val hotel: String?,
    val conference: String,
    // Schedule
    val defaultStatus: String? = null,
    val depth: Int = -1,
    val hierExtentLeft: Int = -1,
    val hierExtentRight: Int = -1,
    val parent: Long = -1,
    val peerSortOrder: Int = -1,
    val schedule: List<LocationSchedule>? = null,
) : Parcelable