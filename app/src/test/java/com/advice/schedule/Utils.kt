package com.advice.schedule

import com.advice.schedule.utilities.Time
import com.google.firebase.Timestamp
import io.mockk.every
import io.mockk.mockkStatic
import java.text.SimpleDateFormat
import java.util.*

fun setCurrentClock(date: String) {
    mockkStatic("com.advice.schedule.utilities.MyClockKt")
    every {
        Time.now()
    } returns parse(date)
}

fun parse(date: String): Date {
    return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").parse(date)
}

fun fromString(date: String): Timestamp {
    return Timestamp(SimpleDateFormat("yyyy-MM-dd").parse(date))
}
