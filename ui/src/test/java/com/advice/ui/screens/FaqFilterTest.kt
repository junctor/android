package com.advice.ui.screens

import com.advice.core.local.FAQ
import org.junit.Assert.assertEquals
import org.junit.Test

class FaqFilterTest {

    private val faqs = listOf(
        FAQ("What is DEF CON?", "A hacking conference."),
        FAQ("Where is Vegas?", "Nevada."),
    )

    @Test
    fun shortQueryReturnsAllFaqs() {
        assertEquals(faqs, filterFaqs(faqs, ""))
        assertEquals(faqs, filterFaqs(faqs, "d"))
    }

    @Test
    fun filtersByQuestionOrAnswerCaseInsensitive() {
        assertEquals(listOf(faqs[0]), filterFaqs(faqs, "def"))
        assertEquals(listOf(faqs[1]), filterFaqs(faqs, "nevada"))
    }
}
