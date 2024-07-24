package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseSession(
    @PropertyName("begin_timestamp")
    val begin_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("begin_tsz")
    val begin_tsz: String = "",
    @PropertyName("channel_id")
    val channel_id: Long? = null,
    @PropertyName("end_timestamp")
    val end_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("end_tsz")
    val end_tsz: String = "",
    @PropertyName("location_id")
    val location_id: Long = -1,
    @PropertyName("recordingpolicy_id")
    val recordingpolicy_id: Long? = null,
    @PropertyName("session_id")
    val session_id: Long = -1,
    @PropertyName("timezone_name")
    val timezone_name: String = "",
) : Parcelable
