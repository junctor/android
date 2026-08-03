package com.advice.analytics.core

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureVersionGatingTest {
    @Test
    fun `version 1 is always enabled`() {
        assertTrue(isMinVersionEnabled(appVersion = 1, minVersion = 99))
        assertTrue(isMinVersionEnabled(appVersion = 1, minVersion = 1))
    }

    @Test
    fun `version at or above min is enabled`() {
        assertTrue(isMinVersionEnabled(appVersion = 10, minVersion = 10))
        assertTrue(isMinVersionEnabled(appVersion = 11, minVersion = 10))
    }

    @Test
    fun `version below min is disabled`() {
        assertFalse(isMinVersionEnabled(appVersion = 9, minVersion = 10))
        assertFalse(isMinVersionEnabled(appVersion = 2, minVersion = 5))
    }
}
