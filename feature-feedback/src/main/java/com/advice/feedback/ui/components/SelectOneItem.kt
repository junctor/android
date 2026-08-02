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
    onSelectOption: (Long?) -> Unit,
) {
    var choice by remember { mutableStateOf(selection) }

    fun toggle(optionId: Long) {
        val next = if (choice == optionId) null else optionId
        choice = next
        onSelectOption(next)
    }

    Column(Modifier.fillMaxWidth()) {
        if (caption.isNotBlank()) {
            Text(caption)
        }

        when {
            // Single option (e.g. acknowledgment) — radio beside label
            options.size <= 1 -> {
                options.forEach { option ->
                    SelectOneOptionRow(
                        option = option,
                        selected = choice == option.id,
                        onSelect = { toggle(option.id) },
                    )
                }
            }

            // Few options — rating-style columns
            options.size <= 3 -> {
                Row(Modifier.fillMaxWidth()) {
                    options.forEach { option ->
                        Column(
                            modifier =
                                Modifier
                                    .clickable { toggle(option.id) }
                                    .weight(1f),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            RadioButton(
                                selected = choice == option.id,
                                onClick = { toggle(option.id) },
                            )
                            Text(
                                text = option.value,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }

            // Many options — vertical list
            else -> {
                options.forEach { option ->
                    SelectOneOptionRow(
                        option = option,
                        selected = choice == option.id,
                        onSelect = { toggle(option.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectOneOptionRow(
    option: FeedbackOption,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .clickable(onClick = onSelect)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
        )
        Text(text = option.value)
    }
}

@PreviewLightDark
@Composable
private fun SelectOneItemPreview() {
    ScheduleTheme {
        SelectOneItem(
            caption = "Select one item",
            options =
                listOf(
                    FeedbackOption(1, "Option 1"),
                    FeedbackOption(2, "Option 2"),
                    FeedbackOption(3, "Option 3"),
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
            options =
                listOf(
                    FeedbackOption(1, "Option 1"),
                    FeedbackOption(2, "Option 2"),
                    FeedbackOption(3, "Option 3"),
                    FeedbackOption(4, "Option 4"),
                ),
            onSelectOption = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun SelectOneItemSingleOptionPreview() {
    ScheduleTheme {
        SelectOneItem(
            caption = "",
            options = listOf(FeedbackOption(1, "OK")),
            selection = 1,
            onSelectOption = {},
        )
    }
}
