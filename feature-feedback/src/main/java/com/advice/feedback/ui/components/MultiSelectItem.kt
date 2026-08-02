package com.advice.feedback.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.advice.core.local.feedback.FeedbackOption
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun MultiSelectItem(
    caption: String,
    options: List<FeedbackOption>,
    onSelectOption: (Long) -> Unit,
) {
    var choices by remember { mutableStateOf(emptyList<Long>()) }

    Column(Modifier.fillMaxWidth()) {
        Text(caption)

        options.forEach { option ->
            val checked = option.id in choices
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .toggleable(
                            value = checked,
                            role = Role.Checkbox,
                            onValueChange = { isChecked ->
                                choices =
                                    if (isChecked) {
                                        choices + option.id
                                    } else {
                                        choices - option.id
                                    }
                                onSelectOption(option.id)
                            },
                        ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = null,
                )
                Text(option.value)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun MultiSelectItemPreview() {
    ScheduleTheme {
        MultiSelectItem(
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
