package com.advice.schedule.utils

import android.annotation.SuppressLint
import android.content.Context
import com.shortstack.hackertracker.BuildConfig
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

object TimeUtils {

    @SuppressLint("SimpleDateFormat")
    fun getDateStamp(date: Date): String {
        val format = SimpleDateFormat("MMMM d")

        return format.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    fun getTimeStamp(context: Context, date: Instant?): String {
        // No start time, return TBA.
        if (date == null)
            return context.getString(com.shortstack.hackertracker.R.string.tba)


        val s = if (android.text.format.DateFormat.is24HourFormat(context)) {
            "HH:mm"
        } else {
            "h:mm\naa"
        }

        val formatter = SimpleDateFormat(s)

        // todo:
//        if (App.instance.storage.forceTimeZone) {
//            val timezone = App.instance.database.conference.value?.timezone ?: "America/Los_Angeles"
//            formatter.timeZone = TimeZone.getTimeZone(timezone)
//        }

        return formatter.format(date)
    }

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
