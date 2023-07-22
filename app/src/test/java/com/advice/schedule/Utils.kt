package com.advice.schedule

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

fun setCurrentClock(date: String) {
}

fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}

fun fromString(date: String): Timestamp {
    return Timestamp(SimpleDateFormat("yyyy-MM-dd").parse(date))
}
