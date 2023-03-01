package com.advice.schedule.utilities

import com.shortstack.hackertracker.BuildConfig
import java.text.SimpleDateFormat
import java.util.Date

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
