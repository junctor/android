package com.advice.core.local.feedback

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class ContentFeedbackFormTest {
    private val form =
        FeedbackForm(
            id = 1,
            conference = 1,
            title = "Session Feedback",
            items = emptyList(),
            endpoint = "https://example.com",
        )

    private val now = Instant.parse("2024-08-10T12:00:00Z")

    @Test
    fun bothNull_isEnabled() {
        val feedback = ContentFeedbackForm(enable = null, disable = null, form = form)

        assertTrue(feedback.isEnabled(now))
    }

    @Test
    fun beforeEnable_isDisabled() {
        val feedback =
            ContentFeedbackForm(
                enable = Instant.parse("2024-08-10T13:00:00Z"),
                disable = null,
                form = form,
            )

        assertFalse(feedback.isEnabled(now))
    }

    @Test
    fun afterDisable_isDisabled() {
        val feedback =
            ContentFeedbackForm(
                enable = Instant.parse("2024-08-10T10:00:00Z"),
                disable = Instant.parse("2024-08-10T11:00:00Z"),
                form = form,
            )

        assertFalse(feedback.isEnabled(now))
    }

    @Test
    fun insideWindow_isEnabled() {
        val feedback =
            ContentFeedbackForm(
                enable = Instant.parse("2024-08-10T11:00:00Z"),
                disable = Instant.parse("2024-08-10T13:00:00Z"),
                form = form,
            )

        assertTrue(feedback.isEnabled(now))
    }
}
