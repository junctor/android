package com.advice.feedback.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.advice.core.local.feedback.FeedbackOption
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun SelectOneItem(
    caption: String,
    options: List<FeedbackOption>,
    selection: Long? = null,
    onSelectOption: (Long) -> Unit,
) {
    var choice by remember { mutableStateOf(selection) }

    Column(Modifier.fillMaxWidth()) {
        Text(caption)

        if (options.size > 3) {
            // Vertical layout
            options.forEach {
                Row(
                    modifier = Modifier
                        .clickable {
                            choice = it.id
                            onSelectOption(it.id)
                        }
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(
                        selected = choice == it.id,
                        onClick = {
                            choice = it.id
                            onSelectOption(it.id)
                        },
                    )
                    Text(
                        text = it.value,
                    )
                }
            }
        } else {
            // Horizontal layout
            Row(Modifier.fillMaxWidth()) {
                options.forEach {
                    Column(
                        modifier = Modifier
                            .clickable {
                                choice = it.id
                                onSelectOption(it.id)
                            }
                            .weight(1f),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        RadioButton(
                            selected = choice == it.id,
                            onClick = {
                                choice = it.id
                                onSelectOption(it.id)
                            },
                        )
                        Text(
                            text = it.value,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FeedbackOption(
    choice: Long?,
    feedbackOption: FeedbackOption,
    onSelectOption: (Long) -> Unit,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Start,
) {
    RadioButton(
        selected = choice == feedbackOption.id,
        onClick = {
            onSelectOption(feedbackOption.id)
        },
    )
    Text(
        text = feedbackOption.value,
        modifier = modifier,
        textAlign = textAlign,
    )
}

@PreviewLightDark
@Composable
private fun SelectOneItemPreview() {
    ScheduleTheme {
        SelectOneItem(
            caption = "Select one item",
            options = listOf(
                FeedbackOption(1, "Option 1"),
                FeedbackOption(2, "Option 2"),
                FeedbackOption(3, "Option 3")
            ),
            onSelectOption = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun SelectOneItemVerticalPreview() {
    ScheduleTheme {
        SelectOneItem(
            caption = "Select one item",
            options = listOf(
                FeedbackOption(1, "Option 1"),
                FeedbackOption(2, "Option 2"),
                FeedbackOption(3, "Option 3"),
                FeedbackOption(4, "Option 4"),
            ),
            onSelectOption = {},
        )
    }
}
