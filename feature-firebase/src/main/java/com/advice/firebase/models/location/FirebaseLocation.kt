package com.advice.firebase.models.location

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseLocation(
    val id: Long = -1,
    val name: String = "",
    // Schedule
    val default_status: String? = null,
    val hier_depth: Int = -1,
    val hier_extent_left: Int = -1,
    val hier_extent_right: Int = -1,
    val parent_id: Long = -1,
    val peer_sort_order: Int = -1,
    val schedule: List<FirebaseLocationSchedule>? = null,
    val short_name: String? = null,
) : Parcelable
