package com.advice.core.utils

import android.content.Context
import com.advice.core.local.Conference
import com.advice.core.local.Event
import com.shortstack.core.BuildConfig
import timber.log.Timber
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.TimeZone

object TimeUtil {

    private fun getZoneId(context: Context, timezone: String): ZoneId? {
        val forceTimeZone =
            context.getSharedPreferences(Storage.KEY_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(Storage.FORCE_TIME_ZONE_KEY, true)
        return getZoneId(forceTimeZone, timezone)
    }

    private fun getZoneId(forceTimeZone: Boolean, timeZone: String): ZoneId? {
        if (forceTimeZone) {
            try {
                return ZoneId.of(timeZone)
            } catch (ex: Exception) {
                Timber.e("Error getting zone id for \"$timeZone\".")
            }
        }

        // forcing to Paris
        if (BuildConfig.DEBUG) {
            return ZoneId.of("Europe/Paris")
        }

        return ZoneId.of(TimeZone.getDefault().id)
    }

    fun getDateStamp(event: Event, forceTimeZone: Boolean): String {
        val zoneId = getZoneId(forceTimeZone, event.timeZone)

        val formatter = DateTimeFormatter.ofPattern("MMMM d")
        val localDateTime = event.start.atZone(zoneId)
        return formatter.format(localDateTime)
    }

    fun getDateTimeStamp(context: Context, event: Event): String {
        val zoneId = getZoneId(context, event.timeZone)
        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)

        val s = if (is24HourFormat) {
            "HH:mm"
        } else {
            "h:mm a"
        }

        val dayFormat = DateTimeFormatter.ofPattern("EEEE, MMMM d")

        val prefix = dayFormat.format(event.start.atZone(zoneId))
        val timeFormat = DateTimeFormatter.ofPattern(s)
        return prefix + " - " + timeFormat.format(event.start.atZone(zoneId)) + " to " + timeFormat.format(
            event.end.atZone(zoneId)
        )
    }

    fun getTimeStamp(context: Context, event: Event): String {
        val zoneId = getZoneId(context, event.timeZone)
        val is24HourFormat = android.text.format.DateFormat.is24HourFormat(context)

        val pattern = if (is24HourFormat) {
            "HH:mm"
        } else {
            "h:mm a"
        }
        val formatter = DateTimeFormatter.ofPattern(pattern)
        val localDateTime = event.start.atZone(zoneId)
        return formatter.format(localDateTime)
    }

    fun getConferenceDateRange(context: Context, conference: Conference): String {
        val zoneId = getZoneId(context, conference.timezone)

        val startFormat = DateTimeFormatter.ofPattern("MMMM d")
        val endFormat = DateTimeFormatter.ofPattern("MMMM d, yyyy")
        return "${startFormat.format(conference.start.atZone(zoneId))} - ${
            endFormat.format(
                conference.end.atZone(zoneId)
            )
        }"
    }
}
