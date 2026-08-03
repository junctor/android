package com.advice.core.utils

import org.junit.Assert.assertNotEquals
import org.junit.Test

class NotificationHelperPendingIntentTest {
    @Test
    fun `pending intent request codes differ by kind and id`() {
        val eventA = NotificationHelper.pendingIntentRequestCode("event", 1L)
        val eventB = NotificationHelper.pendingIntentRequestCode("event", 2L)
        val documentA = NotificationHelper.pendingIntentRequestCode("document", 1L)

        assertNotEquals(eventA, eventB)
        assertNotEquals(eventA, documentA)
        assertNotEquals(eventB, documentA)
    }
}
