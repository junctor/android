package com.advice.core.utils

import android.content.Context
import com.advice.core.local.Conference
import com.advice.core.local.LocationSchedule
import com.advice.core.local.Session
import com.shortstack.core.BuildConfig
import timber.log.Timber
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.TimeZone

object TimeUtil {
    private val formatterCache = HashMap<String, DateTimeFormatter>()
    private val zoneIdCache = HashMap<Pair<Boolean, String>, ZoneId?>()

    private fun formatter(pattern: String): DateTimeFormatter =
        formatterCache.getOrPut(pattern) {
            DateTimeFormatter.ofPattern(pattern)
        }

    private fun getZoneId(
        context: Context,
        timezone: String,
    ): ZoneId? {
        val forceTimeZone =
            context
                .getSharedPreferences(Storage.KEY_PREFERENCES, Context.MODE_PRIVATE)
                .getBoolean(Storage.FORCE_TIME_ZONE_KEY, true)
        return getZoneId(forceTimeZone, timezone)
    }

    private fun getZoneId(
        forceTimeZone: Boolean,
        timeZone: String,
    ): ZoneId? {
        return zoneIdCache.getOrPut(forceTimeZone to timeZone) {
            if (forceTimeZone) {
                try {
                    return@getOrPut ZoneId.of(timeZone)
                } catch (_: Exception) {
                    Timber.e("Error getting zone id for \"$timeZone\".")
                }
            }

            // forcing to Paris
            if (BuildConfig.DEBUG) {
                return@getOrPut ZoneId.of("Europe/Paris")
            }

            ZoneId.of(TimeZone.getDefault().id)
        }
    }

    fun getDateStamp(
        session: Session,
        forceTimeZone: Boolean,
    ): String {
        val zoneId = getZoneId(forceTimeZone, session.timeZone)

        val localDateTime = session.start.atZone(zoneId)
        return formatter("MMMM d").format(localDateTime)
    }

    fun getEventDateStamp(
        context: Context,
        session: Session?,
    ): String {
        if (session == null) {
            return ""
        }

        val zoneId = getZoneId(context, session.timeZone)

        val suffixFormat = formatter("EEE, MMM d, yyyy")

        // If the event is on the same day, we don't need to show the date twice.
        if (isSameDay(session.start, session.end)) {
            return suffixFormat.format(session.start.atZone(zoneId))
        }

        val prefixFormat = formatter("EEE, MMM d")
        // Show the date range.
        return prefixFormat.format(session.start.atZone(zoneId)) + " - " +
            suffixFormat.format(
                session.end.atZone(
                    zoneId,
                ),
            )
    }

    fun getEventTimeStamp(
        context: Context,
        session: Session?,
    ): String {
        if (session == null) {
            return ""
        }

        val zoneId = getZoneId(context, session.timeZone)
        val is24HourFormat =
            android.text.format.DateFormat
                .is24HourFormat(context)
        val pattern =
            if (is24HourFormat) {
                "HH:mm"
            } else {
                "h:mm a"
            }

        val timeFormat = formatter(pattern)

        // If the start time and end time are the same, we don't need to show the end time.
        if (session.start == session.end) {
            return timeFormat.format(session.start.atZone(zoneId))
        }

        return timeFormat.format(session.start.atZone(zoneId)) + " - " +
            timeFormat.format(
                session.end.atZone(
                    zoneId,
                ),
            )
    }

    fun getDateTimeStamp(
        context: Context,
        session: Session,
    ): String {
        val zoneId = getZoneId(context, session.timeZone)
        val is24HourFormat =
            android.text.format.DateFormat
                .is24HourFormat(context)

        val s =
            if (is24HourFormat) {
                "HH:mm"
            } else {
                "h:mm a"
            }

        val prefix = formatter("EEEE, MMMM d").format(session.start.atZone(zoneId))
        val timeFormat = formatter(s)
        return prefix + " - " + timeFormat.format(session.start.atZone(zoneId)) + " to " +
            timeFormat.format(
                session.end.atZone(zoneId),
            )
    }

    fun getTimeStamp(
        context: Context,
        session: Session,
    ): String {
        val zoneId = getZoneId(context, session.timeZone)
        val is24HourFormat =
            android.text.format.DateFormat
                .is24HourFormat(context)

        val pattern =
            if (is24HourFormat) {
                "HH:mm"
            } else {
                "h:mm a"
            }
        val localDateTime = session.start.atZone(zoneId)
        return formatter(pattern).format(localDateTime)
    }

    fun getConferenceDateRange(
        context: Context,
        conference: Conference,
    ): String {
        val zoneId = getZoneId(context, conference.timezone)

        val startFormat = formatter("MMMM d")
        val endFormat = formatter("MMMM d, yyyy")
        return "${startFormat.format(conference.start.atZone(zoneId))} - ${
            endFormat.format(conference.end.atZone(zoneId))
        }"
    }

    fun getScheduleDateStamp(
        context: Context,
        location: LocationSchedule,
        timezone: String,
    ): String {
        val zoneId = getZoneId(context, timezone)
        return formatter("EEEE, MMMM d").format(location.start.atZone(zoneId))
    }

    fun getScheduleDateStamp(
        location: LocationSchedule,
        timezone: String,
        forceTimeZone: Boolean = true,
    ): String {
        val zoneId = getZoneId(forceTimeZone, timezone)
        return formatter("EEEE, MMMM d").format(location.start.atZone(zoneId))
    }

    fun getScheduleTimestamp(
        context: Context,
        location: LocationSchedule,
        timezone: String,
    ): String {
        val is24HourFormat =
            android.text.format.DateFormat
                .is24HourFormat(context)
        return getScheduleTimestamp(location, timezone, is24HourFormat)
    }

    fun getScheduleTimestamp(
        location: LocationSchedule,
        timezone: String,
        is24HourFormat: Boolean,
        forceTimeZone: Boolean = true,
    ): String {
        val zoneId = getZoneId(forceTimeZone, timezone)

        val pattern =
            if (is24HourFormat) {
                "HH:mm"
            } else {
                "h:mm a"
            }
        val timeFormat = formatter(pattern)

        return location.status.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() } + ": " +
            timeFormat.format(
                location.start.atZone(
                    zoneId,
                ),
            ) + " to " +
            timeFormat.format(
                location.end.atZone(zoneId),
            )
    }

    fun getNewsTimestamp(date: Date): String = formatter("MMMM d, yyyy").format(date.toInstant().atZone(ZoneId.systemDefault()))

    private fun isSameDay(
        instant1: Instant,
        instant2: Instant,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Boolean {
        val localDate1 = instant1.atZone(zoneId).toLocalDate()
        val localDate2 = instant2.atZone(zoneId).toLocalDate()

        return localDate1.isEqual(localDate2)
    }
}
