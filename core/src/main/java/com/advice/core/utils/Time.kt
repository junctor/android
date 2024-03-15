package com.advice.core.utils

import com.shortstack.core.BuildConfig
import java.util.Date

@Deprecated("Use Instant instead.")
object Time {
    fun now(): Date {
        if (BuildConfig.DEBUG) {
//            return parse("2022-08-13T01:00:00.000-0000")
        }
        return Date()
    }
}
