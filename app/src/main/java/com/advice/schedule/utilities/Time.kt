package com.advice.schedule.utilities

import com.advice.schedule.App
import com.google.firebase.Timestamp
import com.shortstack.hackertracker.BuildConfig
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

object Time {
    fun now(): Date {
        if (BuildConfig.DEBUG) {
//            return parse("2022-08-13T01:00:00.000-0000")
        }
        return Date()
    }
}


private fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}


fun getDateMidnight(date: Date): Date {
    val calendar = if (App.instance.storage.forceTimeZone) {
        // using the conferences default zone
        Calendar.getInstance(TimeZone.getTimeZone(App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"))
    } else {
        Calendar.getInstance()
    }

    val apply = calendar.apply {
        time = date

        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return apply.time
}

fun getLocalizedDate(date: Date): Date {
    val calendar = if (App.instance.storage.forceTimeZone) {
        // using the conferences default zone
        Calendar.getInstance(TimeZone.getTimeZone(App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"))
    } else {
        Calendar.getInstance()
    }


    val apply = calendar.apply {
        time = date
    }
    return apply.time
}

fun Date.toTimestamp() = Timestamp(this)