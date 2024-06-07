package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Parcelize
data class FirebaseSession(
    @PropertyName("channel_id")
    val channel_id: Long? = null,
    @PropertyName("location_id")
    val location_id: Long = -1,
    @PropertyName("recordingpolicy_id")
    val recordingpolicy_id: Long? = null,
    @PropertyName("session_id")
    val session_id: Long = -1,
    @PropertyName("begin_timestamp")
    val begin_timestamp: Timestamp = Timestamp.now(),
    @PropertyName("end_timestamp")
    val end_timetimestamp: Timestamp = Timestamp.now(),
    @PropertyName("timezone_name")
    val timezone_name: String = "",
) : Parcelable
