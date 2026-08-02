package com.advice.core.local

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ReminderMinutesTest {
    @Test
    fun sanitizeEvent_keepsValidPresets() {
        assertEquals(ReminderMinutes.DISABLED, ReminderMinutes.sanitizeEvent(ReminderMinutes.DISABLED))
        assertEquals(5, ReminderMinutes.sanitizeEvent(5))
        assertEquals(20, ReminderMinutes.sanitizeEvent(20))
        assertEquals(60, ReminderMinutes.sanitizeEvent(60))
    }

    @Test
    fun sanitizeEvent_fallsBackToDefaultForInvalidValues() {
        assertEquals(ReminderMinutes.DEFAULT, ReminderMinutes.sanitizeEvent(0))
        assertEquals(ReminderMinutes.DEFAULT, ReminderMinutes.sanitizeEvent(7))
        assertEquals(ReminderMinutes.DEFAULT, ReminderMinutes.sanitizeEvent(100))
    }

    @Test
    fun sanitizeFeedback_allowsDisabledAndZero() {
        assertEquals(ReminderMinutes.DISABLED, ReminderMinutes.sanitizeFeedback(ReminderMinutes.DISABLED))
        assertEquals(0, ReminderMinutes.sanitizeFeedback(0))
        assertEquals(15, ReminderMinutes.sanitizeFeedback(15))
    }

    @Test
    fun sanitizeFeedback_fallsBackToDefaultForInvalidValues() {
        assertEquals(ReminderMinutes.DEFAULT, ReminderMinutes.sanitizeFeedback(3))
        assertEquals(ReminderMinutes.DEFAULT, ReminderMinutes.sanitizeFeedback(90))
    }

    @Test
    fun isDisabled_onlyForDisabledConstant() {
        assertTrue(ReminderMinutes.isDisabled(ReminderMinutes.DISABLED))
        assertFalse(ReminderMinutes.isDisabled(0))
        assertFalse(ReminderMinutes.isDisabled(20))
    }

    @Test
    fun presets_includeExpectedValues() {
        assertEquals(listOf(-1) + (5..60 step 5).toList(), ReminderMinutes.eventPresets)
        assertEquals(listOf(-1, 0) + (5..60 step 5).toList(), ReminderMinutes.feedbackPresets)
    }
}
