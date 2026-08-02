package com.advice.firebase.extensions

import com.advice.core.local.Bookmark
import com.advice.core.local.Location
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.feedback.FeedbackForm
import com.advice.firebase.models.FirebaseContent
import com.advice.firebase.models.FirebasePerson
import com.advice.firebase.models.FirebaseSession
import com.google.firebase.Timestamp
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date

class ContentMapperSessionTest {
    private val location = Location(10, "Track A", "A")
    private val role = Tag(1, "Speaker", "", null, 0)
    private val tagTypes =
        listOf(
            TagType(1, "Roles", "people", true, 0, listOf(role)),
        )

    @Test
    fun missingLocation_dropsSession() {
        val content =
            firebaseContent(
                sessions =
                    arrayListOf(
                        firebaseSession(sessionId = 1, locationId = 999),
                        firebaseSession(sessionId = 2, locationId = location.id),
                    ),
            )

        val mapped =
            content.toContents(
                code = "TEST",
                tags = tagTypes,
                speakers = emptyList(),
                bookmarkedEvents = emptyList(),
                locations = listOf(location),
                feedbackforms = emptyList(),
            )

        assertNotNull(mapped)
        assertEquals(listOf(2L), mapped!!.sessions.map { it.id })
    }

    @Test
    fun sessionAndContentBookmarks_areApplied() {
        val content =
            firebaseContent(
                id = 5,
                sessions = arrayListOf(firebaseSession(sessionId = 1, locationId = location.id)),
            )

        val mapped =
            content.toContents(
                code = "TEST",
                tags = tagTypes,
                speakers = emptyList(),
                bookmarkedEvents =
                    listOf(
                        Bookmark.ContentBookmark("5", true),
                        Bookmark.SessionBookmark("1", true),
                    ),
                locations = listOf(location),
                feedbackforms = emptyList(),
            )

        assertTrue(mapped!!.isBookmarked)
        assertTrue(mapped.sessions.single().isBookmarked)
    }

    @Test
    fun speakers_sortedBySortOrderThenName() {
        val speakers =
            listOf(
                Speaker(
                    id = 1,
                    name = "Zoe",
                    pronouns = null,
                    description = "",
                    affiliations = emptyList(),
                    links = emptyList(),
                    roles = emptyList(),
                ),
                Speaker(
                    id = 2,
                    name = "Ada",
                    pronouns = null,
                    description = "",
                    affiliations = emptyList(),
                    links = emptyList(),
                    roles = emptyList(),
                ),
                Speaker(
                    id = 3,
                    name = "Bob",
                    pronouns = null,
                    description = "",
                    affiliations = emptyList(),
                    links = emptyList(),
                    roles = emptyList(),
                ),
            )
        val content =
            firebaseContent(
                people =
                    arrayListOf(
                        FirebasePerson(personId = 1, sortOrder = 2),
                        FirebasePerson(personId = 2, sortOrder = 1),
                        FirebasePerson(personId = 3, sortOrder = 1),
                    ),
                sessions = arrayListOf(firebaseSession(sessionId = 1, locationId = location.id)),
            )

        val mapped =
            content.toContents(
                code = "TEST",
                tags = tagTypes,
                speakers = speakers,
                bookmarkedEvents = emptyList(),
                locations = listOf(location),
                feedbackforms = emptyList(),
            )

        assertEquals(listOf("Ada", "Bob", "Zoe"), mapped!!.speakers.map { it.name })
    }

    @Test
    fun feedbackForm_linkedById() {
        val form =
            FeedbackForm(
                id = 9,
                conference = 1,
                title = "Session Feedback",
                items = emptyList(),
                endpoint = "https://example.com",
            )
        val content =
            firebaseContent(
                feedbackFormId = 9,
                sessions = arrayListOf(firebaseSession(sessionId = 1, locationId = location.id)),
            )

        val mapped =
            content.toContents(
                code = "TEST",
                tags = tagTypes,
                speakers = emptyList(),
                bookmarkedEvents = emptyList(),
                locations = listOf(location),
                feedbackforms = listOf(form),
            )

        assertEquals(9L, mapped!!.feedback!!.form.id)
        assertFalse(mapped.isBookmarked)
    }

    @Test
    fun unknownFeedbackFormId_leavesFeedbackNull() {
        val content =
            firebaseContent(
                feedbackFormId = 99,
                sessions = arrayListOf(firebaseSession(sessionId = 1, locationId = location.id)),
            )

        val mapped =
            content.toContents(
                code = "TEST",
                tags = tagTypes,
                speakers = emptyList(),
                bookmarkedEvents = emptyList(),
                locations = listOf(location),
                feedbackforms = emptyList(),
            )

        assertNull(mapped!!.feedback)
    }

    private fun firebaseContent(
        id: Long = 1,
        sessions: ArrayList<FirebaseSession> = arrayListOf(),
        people: ArrayList<FirebasePerson> = arrayListOf(),
        feedbackFormId: Long? = null,
    ) = FirebaseContent(
        id = id,
        title = "Talk",
        description = "Desc",
        sessions = sessions,
        people = people,
        feedbackFormId = feedbackFormId,
        updatedTimestamp = Timestamp(Date.from(java.time.Instant.parse("2024-08-01T00:00:00Z"))),
    )

    private fun firebaseSession(
        sessionId: Long,
        locationId: Long,
    ) = FirebaseSession(
        sessionId = sessionId,
        locationId = locationId,
        timezoneName = "UTC",
        beginTimestamp = Timestamp(Date.from(java.time.Instant.parse("2024-08-10T18:00:00Z"))),
        endTimestamp = Timestamp(Date.from(java.time.Instant.parse("2024-08-10T19:00:00Z"))),
    )
}
