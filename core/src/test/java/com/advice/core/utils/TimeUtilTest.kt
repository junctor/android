package com.advice.core.utils

import com.advice.core.local.LocationSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class TimeUtilTest {
    @Test
    fun getScheduleDateStamp_usesConferenceTimezoneAcrossUtcDayBoundary() {
        // 2024-08-10 02:00 UTC == 2024-08-09 evening in America/New_York
        val schedule =
            LocationSchedule(
                start = Instant.parse("2024-08-10T02:00:00Z"),
                end = Instant.parse("2024-08-10T04:00:00Z"),
                notes = "",
                status = "open",
            )

        val eastern =
            TimeUtil.getScheduleDateStamp(
                location = schedule,
                timezone = "America/New_York",
                forceTimeZone = true,
            )
        val utc =
            TimeUtil.getScheduleDateStamp(
                location = schedule,
                timezone = "UTC",
                forceTimeZone = true,
            )

        assertEquals("Friday, August 9", eastern)
        assertEquals("Saturday, August 10", utc)
        assertFalse(eastern == utc)
    }

    @Test
    fun getScheduleTimestamp_formatsWithSuppliedTimezoneAndClockPreference() {
        val schedule =
            LocationSchedule(
                start = Instant.parse("2024-08-10T18:30:00Z"),
                end = Instant.parse("2024-08-10T20:00:00Z"),
                notes = "",
                status = "open",
            )

        val twelveHour =
            TimeUtil.getScheduleTimestamp(
                location = schedule,
                timezone = "America/New_York",
                is24HourFormat = false,
                forceTimeZone = true,
            )
        val twentyFourHour =
            TimeUtil.getScheduleTimestamp(
                location = schedule,
                timezone = "America/New_York",
                is24HourFormat = true,
                forceTimeZone = true,
            )

        assertTrue(twelveHour.startsWith("Open: "))
        assertTrue(twelveHour.contains("2:30 PM"))
        assertTrue(twelveHour.contains("4:00 PM"))
        assertTrue(twentyFourHour.contains("14:30"))
        assertTrue(twentyFourHour.contains("16:00"))
    }
}
