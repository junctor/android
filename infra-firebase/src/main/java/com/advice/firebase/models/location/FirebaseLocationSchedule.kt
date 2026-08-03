package com.advice.firebase.models.location

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseLocationSchedule(
    @get:PropertyName("begin")
    @set:PropertyName("begin")
    var begin: String = "",
    @get:PropertyName("end")
    @set:PropertyName("end")
    var end: String = "",
    @get:PropertyName("notes")
    @set:PropertyName("notes")
    var notes: String? = null,
    @get:PropertyName("status")
    @set:PropertyName("status")
    var status: String = "closed",
    @get:PropertyName("begin_tsz")
    @set:PropertyName("begin_tsz")
    var beginTsz: String? = null,
    @get:PropertyName("end_tsz")
    @set:PropertyName("end_tsz")
    var endTsz: String? = null,
) : Parcelable
