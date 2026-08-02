package com.advice.core.local

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class EventIdTest {
    @Test
    fun `eventId is contentId colon sessionId`() {
        val location = Location(1, "Track A", "A")
        val session =
            Session(
                id = 42,
                timeZone = "UTC",
                start = Instant.parse("2024-08-10T18:00:00Z"),
                end = Instant.parse("2024-08-10T19:00:00Z"),
                location = location,
            )
        val content =
            Content(
                id = 7,
                conference = "TEST",
                title = "Talk",
                description = "",
                updated = Instant.parse("2024-08-01T00:00:00Z"),
                speakers = emptyList(),
                types = emptyList(),
                urls = emptyList(),
                media = emptyList(),
                sessions = listOf(session),
            )

        assertEquals("7:42", Event(content, session).eventId)
    }
}
