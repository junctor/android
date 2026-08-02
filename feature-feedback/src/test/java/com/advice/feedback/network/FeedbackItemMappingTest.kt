package com.advice.feedback.network

import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackOption
import com.advice.core.local.feedback.FeedbackType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test

class FeedbackItemMappingTest {
    private val options =
        listOf(
            FeedbackOption(1, "A"),
            FeedbackOption(2, "B"),
        )

    @Test
    fun `DisplayOnly maps to null`() {
        val item =
            FeedbackItem(
                id = 1,
                caption = "Info",
                type = FeedbackType.DisplayOnly,
            )

        assertNull(item.toFeedback())
    }

    @Test
    fun `MultiSelect maps selections`() {
        val item =
            FeedbackItem(
                id = 2,
                caption = "Pick",
                type = FeedbackType.MultiSelect(options, selections = listOf(1L, 2L)),
            )

        val feedback = item.toFeedback()!!
        assertEquals(2L, feedback.itemId)
        assertEquals(listOf(1L, 2L), feedback.options)
        assertEquals("", feedback.text)
    }

    @Test
    fun `SelectOne maps selection`() {
        val item =
            FeedbackItem(
                id = 3,
                caption = "One",
                type = FeedbackType.SelectOne(options, selection = 2L),
            )

        val feedback = item.toFeedback()!!
        assertEquals(3L, feedback.itemId)
        assertEquals(listOf(2L), feedback.options)
    }

    @Test
    fun `SelectOne with null selection maps empty options`() {
        val item =
            FeedbackItem(
                id = 4,
                caption = "One",
                type = FeedbackType.SelectOne(options, selection = null),
            )

        assertEquals(emptyList<Long>(), item.toFeedback()!!.options)
    }

    @Test
    fun `TextBox maps text`() {
        val item =
            FeedbackItem(
                id = 5,
                caption = "Notes",
                type = FeedbackType.TextBox("hello"),
            )

        val feedback = item.toFeedback()!!
        assertEquals(5L, feedback.itemId)
        assertEquals("hello", feedback.text)
        assertEquals(emptyList<Long>(), feedback.options)
    }
}
