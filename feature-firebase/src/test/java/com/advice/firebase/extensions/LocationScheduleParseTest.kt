package com.advice.firebase.extensions

import com.advice.firebase.models.location.FirebaseLocationSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class LocationScheduleParseTest {
    @Test
    fun parsesOffsetDateTimeStrings() {
        val schedule =
            FirebaseLocationSchedule(
                begin = "2024-08-10T18:00:00Z",
                end = "2024-08-10T20:00:00Z",
                notes = "open",
                status = "open",
            ).toSchedule()

        assertNotNull(schedule)
        assertEquals(Instant.parse("2024-08-10T18:00:00Z"), schedule!!.start)
        assertEquals(Instant.parse("2024-08-10T20:00:00Z"), schedule.end)
        assertEquals("open", schedule.status)
    }

    @Test
    fun fallsBackToLocalDateTimeInSystemZone() {
        val begin = "2024-08-10T18:00:00"
        val end = "2024-08-10T20:00:00"
        val schedule =
            FirebaseLocationSchedule(
                begin = begin,
                end = end,
                status = "closed",
            ).toSchedule()

        assertNotNull(schedule)
        assertEquals(
            LocalDateTime.parse(begin).atZone(ZoneId.systemDefault()).toInstant(),
            schedule!!.start,
        )
        assertEquals(
            LocalDateTime.parse(end).atZone(ZoneId.systemDefault()).toInstant(),
            schedule.end,
        )
    }

    @Test
    fun invalidStrings_returnNull() {
        assertNull(
            FirebaseLocationSchedule(
                begin = "not-a-date",
                end = "also-bad",
            ).toSchedule(),
        )
    }
}
