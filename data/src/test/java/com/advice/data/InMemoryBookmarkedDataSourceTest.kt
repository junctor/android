package com.advice.data

import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Tag
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Instant

class InMemoryBookmarkedDataSourceTest {
    private val location = Location(1, "Track A", "A")

    @Test
    fun `bookmark and unbookmark session content and tag`() =
        runTest {
            val source = InMemoryBookmarkedDataSourceImpl()
            val session = session(1)
            val content = content(listOf(session))
            val tag = Tag(5, "Talk", "", "#FF0000", 0)

            assertFalse(source.isBookmarked(session))
            assertFalse(source.isBookmarked(content))
            assertFalse(source.isBookmarked(tag))

            source.bookmark(session, true)
            source.bookmark(content, true)
            source.bookmark(tag, true)

            assertTrue(source.isBookmarked(session))
            assertTrue(source.isBookmarked(content))
            assertTrue(source.isBookmarked(tag))
            assertEquals(3, source.get().first().size)

            source.bookmark(session, false)
            assertFalse(source.isBookmarked(session))
            assertEquals(2, source.get().first().size)

            source.clear()
            assertFalse(source.isBookmarked(content))
            assertFalse(source.isBookmarked(tag))
            assertTrue(source.get().first().isEmpty())
        }

    @Test
    fun `get flow emits on bookmark changes`() =
        runTest {
            val source = InMemoryBookmarkedDataSourceImpl()
            assertTrue(source.get().first().isEmpty())

            source.bookmark(session(9), true)
            assertEquals(1, source.get().first().size)

            source.clear()
            assertTrue(source.get().first().isEmpty())
        }

    private fun content(sessions: List<Session>) =
        Content(
            id = 1,
            conference = "TEST",
            title = "Talk",
            description = "",
            updated = Instant.parse("2024-08-01T00:00:00Z"),
            speakers = emptyList(),
            types = emptyList(),
            urls = emptyList(),
            media = emptyList(),
            sessions = sessions,
        )

    private fun session(id: Long) =
        Session(
            id = id,
            timeZone = "UTC",
            start = Instant.parse("2024-08-10T18:00:00Z"),
            end = Instant.parse("2024-08-10T19:00:00Z"),
            location = location,
        )
}
