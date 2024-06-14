package com.advice.feedback.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun MultiSelectItem(
    caption: String,
    options: List<String>,
) {
    var choice by remember { mutableStateOf(options[0]) }

    Column(Modifier.fillMaxWidth()) {
        Text(caption)

        options.forEach {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = choice == it, onCheckedChange = { isChecked ->
                    choice = it
                })
                Text(it)
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun MultiSelectItemPreview() {
    ScheduleTheme {
        MultiSelectItem(
            caption = "Select one item",
            options = listOf("Option 1", "Option 2", "Option 3"),
        )
    }
}
