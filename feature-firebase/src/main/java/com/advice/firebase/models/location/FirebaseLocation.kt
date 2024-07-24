package com.advice.firebase.models.location

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseLocation(
    @get:PropertyName("hotel")
    @set:PropertyName("hotel")
    var hotel: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("default_status")
    @set:PropertyName("default_status")
    var defaultStatus: String? = null,
    @get:PropertyName("hier_depth")
    @set:PropertyName("hier_depth")
    var hierDepth: Int = -1,
    @get:PropertyName("hier_extent_left")
    @set:PropertyName("hier_extent_left")
    var hierExtentLeft: Int = -1,
    @get:PropertyName("hier_extent_right")
    @set:PropertyName("hier_extent_right")
    var hierExtentRight: Int = -1,
    @get:PropertyName("parent_id")
    @set:PropertyName("parent_id")
    var parentId: Long = -1,
    @get:PropertyName("peer_sort_order")
    @set:PropertyName("peer_sort_order")
    var peerSortOrder: Int = -1,
    @get:PropertyName("schedule")
    @set:PropertyName("schedule")
    var schedule: List<FirebaseLocationSchedule>? = null,
    @get:PropertyName("short_name")
    @set:PropertyName("short_name")
    var shortName: String? = null,
) : Parcelable
