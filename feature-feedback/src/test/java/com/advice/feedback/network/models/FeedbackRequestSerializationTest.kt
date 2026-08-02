package com.advice.feedback.network.models

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeedbackRequestSerializationTest {
    private val gson =
        GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .create()

    @Test
    fun omitsContentIdWhenNull() {
        val json =
            gson.toJson(
                FeedbackRequest(
                    feedbackFormId = 1,
                    contentId = null,
                    conferenceId = 2,
                    client = "Android 1.0",
                    deviceId = "device",
                    timestamp = "2026-07-31T12:00:00-04:00",
                    items = emptyList(),
                ),
            )

        assertFalse(json.contains("content_id"))
        assertTrue(json.contains("\"feedback_form_id\":1"))
        assertTrue(json.contains("\"conference_id\":2"))
    }

    @Test
    fun includesContentIdWhenPresent() {
        val json =
            gson.toJson(
                FeedbackRequest(
                    feedbackFormId = 1,
                    contentId = 42,
                    conferenceId = 2,
                    client = "Android 1.0",
                    deviceId = "device",
                    timestamp = "2026-07-31T12:00:00-04:00",
                    items = emptyList(),
                ),
            )

        assertTrue(json.contains("\"content_id\":42"))
    }
}
