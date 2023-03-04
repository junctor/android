package com.advice.core.local

import android.content.Context
import android.os.Parcelable
import com.advice.core.utils.Time
import com.advice.core.utils.TimeUtil
import com.advice.core.utils.getDateMidnight
import com.advice.core.utils.getLocalizedDate
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

@Parcelize
data class Event(
    val id: Long = -1,
    val conference: String,
    val title: String,
    val description: String,
    val start: Date,
    val end: Date,
    val updated: String,
    val speakers: List<Speaker>,
    val types: List<Tag>,
    val location: Location,
    val urls: List<Action>,
    var isBookmarked: Boolean = false,
    var key: Long = -1
) : Parcelable {

    val progress: Float
        get() {
            return 0f
        }

    val hasFinished: Boolean
        get() {
            return end.compareTo(Time.now()) == 1
        }

    val hasStarted: Boolean
        get() = start.compareTo(Time.now()) == -1

    val date: Date
        get() {
            return Calendar.getInstance().apply {
                time = start

                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
        }

    val startTime: Date
        get() {
            return Calendar.getInstance().apply {
                time = start
            }.time
        }

    val adjustedDate: Date
        get() {
            return getDateMidnight(start)
        }

    fun getFullTimeStamp(context: Context): String {
        val localizedDate = getLocalizedDate(start)

        val date = TimeUtil.getDateStamp(localizedDate)

        val time = if (android.text.format.DateFormat.is24HourFormat(context)) {
            SimpleDateFormat("HH:mm").format(localizedDate)
        } else {
            SimpleDateFormat("h:mm aa").format(localizedDate)
        }

        return "TODO"//String.format(context.getString(R.string.timestamp_start), date, time)
    }
}