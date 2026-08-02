package com.advice.schedule.presentation.viewmodel

import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackOption
import com.advice.core.local.feedback.FeedbackType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertSame
import junit.framework.TestCase.assertTrue
import org.junit.Test

class FeedbackValueChangeTest {
    private val options =
        listOf(
            FeedbackOption(1, "A"),
            FeedbackOption(2, "B"),
            FeedbackOption(3, "C"),
        )

    @Test
    fun `MultiSelect toggles add and remove`() {
        val item =
            FeedbackItem(
                id = 10,
                caption = "Pick",
                type = FeedbackType.MultiSelect(options, selections = listOf(1L)),
            )

        val added = applyFeedbackValueChange(listOf(item), item, "2")
        val addedType = added.single().type as FeedbackType.MultiSelect
        assertEquals(listOf(1L, 2L), addedType.selections)

        val removed = applyFeedbackValueChange(added, added.single(), "1")
        val removedType = removed.single().type as FeedbackType.MultiSelect
        assertEquals(listOf(2L), removedType.selections)
    }

    @Test
    fun `SelectOne sets selection`() {
        val item =
            FeedbackItem(
                id = 11,
                caption = "One",
                type = FeedbackType.SelectOne(options, selection = null),
            )

        val updated = applyFeedbackValueChange(listOf(item), item, "3")
        val type = updated.single().type as FeedbackType.SelectOne
        assertEquals(3L, type.selection)
    }

    @Test
    fun `TextBox sets value`() {
        val item =
            FeedbackItem(
                id = 12,
                caption = "Notes",
                type = FeedbackType.TextBox(""),
            )

        val updated = applyFeedbackValueChange(listOf(item), item, "hello")
        val type = updated.single().type as FeedbackType.TextBox
        assertEquals("hello", type.value)
    }

    @Test
    fun `DisplayOnly remains unchanged`() {
        val item =
            FeedbackItem(
                id = 13,
                caption = "Info",
                type = FeedbackType.DisplayOnly,
            )

        val updated = applyFeedbackValueChange(listOf(item), item, "ignored")
        assertSame(FeedbackType.DisplayOnly, updated.single().type)
        assertTrue(updated.single() === item || updated.single() == item)
    }
}
