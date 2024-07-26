package com.advice.feedback.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackOption
import com.advice.core.local.feedback.FeedbackType

class FeedbackFormProvider : PreviewParameterProvider<FeedbackForm> {
    override val values: Sequence<FeedbackForm>
        get() {
            return listOf(
                element,
            ).asSequence()
        }

    companion object {
        val element =
            FeedbackForm(
                id = 1,
                conference = 1,
                title = "Thanks for attending Policy @ DEF CON",
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
                            options = listOf(
                                FeedbackOption(1, "Good"),
                                FeedbackOption(2, "Bad"),
                                FeedbackOption(3, "Neutral")
                            ),
                        ),
                    ),
                    FeedbackItem(
                        id = 2,
                        caption = "What do you think applies?",
                        type =
                        FeedbackType.MultiSelect(
                            options = listOf(
                                FeedbackOption(1, "Hacking"),
                                FeedbackOption(2, "Cool"),
                                FeedbackOption(3, "Hands on"),
                            ),
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
                endpoint = "https://feedback.example.com/submit",
            )
    }
}
