package com.advice.core.local

import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.generalFeedbackForm

fun List<Menu>.withFeedbackGating(forms: List<FeedbackForm>): List<Menu> {
    val feedback = forms.generalFeedbackForm()
    return map { menu ->
        menu.copy(
            items =
                menu.items.mapNotNull { item ->
                    when {
                        item is MenuItem.Navigation && item.function == "feedback" -> {
                            if (feedback != null) {
                                MenuItem.Feedback(
                                    icon = item.icon ?: "",
                                    label = item.label,
                                    description = item.description,
                                    formId = feedback.id,
                                )
                            } else {
                                null
                            }
                        }

                        else -> item
                    }
                },
        )
    }
}

/**
 * Adds a General Feedback entry to the home menu when that form exists.
 * Does not require a Firestore menu item with function="feedback".
 */
fun Menu.withGeneralFeedback(forms: List<FeedbackForm>): Menu {
    val form = forms.generalFeedbackForm() ?: return this
    if (items.any { it is MenuItem.Feedback }) return this
    return copy(
        items =
            items +
                MenuItem.Feedback(
                    icon = "event_note",
                    label = form.title,
                    description = null,
                    formId = form.id,
                ),
    )
}
