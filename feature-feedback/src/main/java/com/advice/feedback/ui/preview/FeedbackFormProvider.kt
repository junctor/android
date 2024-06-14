package com.advice.feedback.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.feedback.models.FeedbackForm
import com.advice.feedback.models.FeedbackItem
import com.advice.feedback.models.FeedbackType

class FeedbackFormProvider : PreviewParameterProvider<FeedbackForm> {
    override val values: Sequence<FeedbackForm>
        get() =
            listOf(
                FeedbackForm(
                    id = 1,
                    items =
                        listOf(
                            FeedbackItem(
                                id = 0,
                                caption = "Thank you so much for attending Policy @ DEF CON, and for taking the time to leave feedback.",
                                type = FeedbackType.DisplayOnly,
                            ),
                            FeedbackItem(
                                id = 1,
                                caption = "How do you feel?",
                                type =
                                    FeedbackType.SelectOne(
                                        options = listOf("Good", "Bad", "Neutral"),
                                    ),
                            ),
                            FeedbackItem(
                                id = 2,
                                caption = "What do you think applies?",
                                type =
                                    FeedbackType.MultiSelect(
                                        options = listOf("Hacking", "Cool", "Hands on"),
                                    ),
                            ),
                            FeedbackItem(
                                id = 3,
                                caption = "What do you think?",
                                type =
                                    FeedbackType.TextBox(
                                        value = "",
                                    ),
                            ),
                        ),
                ),
            ).asSequence()
}
