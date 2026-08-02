package com.advice.core.local

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.GENERAL_FEEDBACK_TITLE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuFeedbackGatingTest {
    private val generalForm =
        FeedbackForm(
            id = 10,
            conference = 1,
            title = GENERAL_FEEDBACK_TITLE,
            items = emptyList(),
            endpoint = "https://example.com/feedback",
        )

    private val otherForm =
        FeedbackForm(
            id = 11,
            conference = 1,
            title = "Session Feedback",
            items = emptyList(),
            endpoint = "https://example.com/feedback",
        )

    private val menusWithFeedback =
        listOf(
            Menu(
                id = 1,
                label = "Home",
                items =
                    listOf(
                        MenuItem.Navigation("feedback", "Feedback", null, "feedback"),
                        MenuItem.Navigation("news", "News", null, "news"),
                    ),
            ),
        )

    private val menusWithoutFeedback =
        listOf(
            Menu(
                id = 1,
                label = "Home",
                items =
                    listOf(
                        MenuItem.Navigation("news", "News", null, "news"),
                    ),
            ),
        )

    @Test
    fun keepsFeedbackItemWhenGeneralFeedbackFormExists() {
        val result = menusWithFeedback.withFeedbackGating(listOf(generalForm, otherForm))

        assertEquals(2, result.single().items.size)
        val feedback = result.single().items.first() as MenuItem.Feedback
        assertEquals(generalForm.id, feedback.formId)
        assertEquals("Feedback", feedback.label)
        assertTrue(result.single().items[1] is MenuItem.Navigation)
    }

    @Test
    fun removesFeedbackItemWhenFormsEmpty() {
        val result = menusWithFeedback.withFeedbackGating(emptyList())

        assertEquals(1, result.single().items.size)
        assertTrue(result.single().items.single() is MenuItem.Navigation)
    }

    @Test
    fun removesFeedbackItemWhenOnlyOtherTitlesPresent() {
        val result = menusWithFeedback.withFeedbackGating(listOf(otherForm))

        assertEquals(1, result.single().items.size)
        assertTrue(result.single().items.single() is MenuItem.Navigation)
    }

    @Test
    fun removesFeedbackItemWhenTitleDiffersByCase() {
        val wrongCase =
            generalForm.copy(title = "general feedback")

        val result = menusWithFeedback.withFeedbackGating(listOf(wrongCase))

        assertEquals(1, result.single().items.size)
        assertTrue(result.single().items.single() is MenuItem.Navigation)
    }

    @Test
    fun injectsGeneralFeedbackWhenFormExistsAndMenuHasNoFeedbackItem() {
        val menu = menusWithoutFeedback.single().withGeneralFeedback(listOf(generalForm))

        assertEquals(2, menu.items.size)
        val feedback = menu.items.last() as MenuItem.Feedback
        assertEquals(generalForm.id, feedback.formId)
        assertEquals(GENERAL_FEEDBACK_TITLE, feedback.label)
    }

    @Test
    fun doesNotInjectGeneralFeedbackWhenFormMissing() {
        val menu = menusWithoutFeedback.single().withGeneralFeedback(listOf(otherForm))

        assertEquals(1, menu.items.size)
        assertTrue(menu.items.single() is MenuItem.Navigation)
    }

    @Test
    fun doesNotDuplicateFeedbackWhenAlreadyPresent() {
        val existing =
            menusWithoutFeedback.single().copy(
                items =
                    menusWithoutFeedback.single().items +
                        MenuItem.Feedback("event_note", GENERAL_FEEDBACK_TITLE, null, generalForm.id),
            )

        val menu = existing.withGeneralFeedback(listOf(generalForm))

        assertEquals(1, menu.items.count { it is MenuItem.Feedback })
    }
}
