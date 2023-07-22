package com.advice.core.utils

import android.annotation.SuppressLint
import android.content.Context
import com.advice.core.local.Event
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@SuppressLint("NewApi")
object TimeUtil {

    // todo: get from conference or user device
    private var timeZone = "America/Los_Angeles"

    fun getDateStamp(date: Instant): String {
        val formatter = DateTimeFormatter.ofPattern("MMMM d")
        val localDateTime = date.atZone(ZoneId.of(timeZone))
        return formatter.format(localDateTime)
    }


    fun getTimeStamp(context: Context, event: Event): String {
        val s = if (android.text.format.DateFormat.is24HourFormat(context)) {
            "HH:mm"
        } else {
            "h:mm\na"
        }

        val dayFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d")
        val prefix = dayFormat.format(event.start.atZone(ZoneId.of(timeZone)))
        val timeFormat = DateTimeFormatter.ofPattern(s)
        return prefix + " - " + timeFormat.format(event.start.atZone(ZoneId.of(timeZone))) + " to " + timeFormat.format(
            event.end.atZone(ZoneId.of(timeZone))
        )
    }

    fun getTimeStamp(date: Instant, is24HourFormat: Boolean): String {
        val pattern = if (is24HourFormat) {
            "HH:mm"
        } else {
            "h:mm a"
        }
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val localDateTime = date.atZone(ZoneId.of(timeZone))
        return formatter.format(localDateTime)
    }
}
