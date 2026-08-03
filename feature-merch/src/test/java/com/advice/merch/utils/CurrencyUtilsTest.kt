package com.advice.merch.utils

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class CurrencyUtilsTest {
    @Test
    fun `toCurrency whole dollars omits cents`() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("US$10", 1000L.toCurrency())
            assertEquals("US$0", 0L.toCurrency())
        } finally {
            Locale.setDefault(previous)
        }
    }

    @Test
    fun `toCurrency shows cents when not divisible by 100`() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("US$10.50", 1050L.toCurrency())
            assertEquals("US$10.00", 1000L.toCurrency(showCents = true))
        } finally {
            Locale.setDefault(previous)
        }
    }

    @Test
    fun `toCurrency showPlus appends plus`() {
        val previous = Locale.getDefault()
        try {
            Locale.setDefault(Locale.US)
            assertEquals("US$10+", 1000L.toCurrency(showPlus = true))
            assertEquals("US$10.50+", 1050L.toCurrency(showPlus = true))
        } finally {
            Locale.setDefault(previous)
        }
    }
}
