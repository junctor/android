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
    @PropertyName("begin_time")
    val begin_time: String = "", // todo: would be nice if these were timestamps.
    @PropertyName("end_time")
    val end_time: String = "",
    @PropertyName("timezone_name")
    val timezone_name: String = "",
) : Parcelable {

    // parse the String using the timezone to a Date then convert to a Timestamp.
    val begin: Timestamp
        get() = Timestamp(parseDateWithTimeZone(begin_time, timezone_name)!!)

    val end: Timestamp
        get() = Timestamp(parseDateWithTimeZone(end_time, timezone_name)!!)

    private fun parseDateWithTimeZone(dateString: String, timeZone: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        format.timeZone = TimeZone.getTimeZone(timeZone)
        return format.parse(dateString)
    }
}
