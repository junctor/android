package com.advice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ButtonPreference(
    title: String,
    options: List<PreferenceOption>,
    summary: String? = null,
    onPreferenceChange: (String) -> Unit,
) {
    var dialogOpen by remember { mutableStateOf(false) }

    if (dialogOpen) {
        PreferenceDialog(
            title = title,
            options = options,
            onOptionSelect = {
                onPreferenceChange(it)
                dialogOpen = false
            },
            onDismiss = {
                dialogOpen = false
            },
        )
    }

    Row(
        Modifier
            .clickable {
                dialogOpen = true
            }.fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp),
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(title)
            if (summary != null) {
                Text(summary)
            }
        }
    }
}

@Composable
private fun PreferenceDialog(
    title: String,
    options: List<PreferenceOption>,
    onOptionSelect: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                for (option in options) {
                    TextButton(onClick = { onOptionSelect(option.value) }) {
                        Text(option.title, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = { },
        tonalElevation = 0.dp,
    )
}

@PreviewLightDark
@Composable
private fun ButtonPreferencePreview() {
    ScheduleTheme {
        ButtonPreference(
            title = "Choose theme",
            options =
                listOf(
                    PreferenceOption("Light", "light"),
                    PreferenceOption("Dark", "dark"),
                    PreferenceOption("System default", "system"),
                ),
            onPreferenceChange = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun ButtonPreferenceWithSummaryPreview() {
    val options =
        listOf(
            PreferenceOption("August 1", "month_day"),
            PreferenceOption("Saturday", "day_of_week"),
            PreferenceOption("Sat, Aug 1", "day_abbr_month_day"),
        )

    ScheduleTheme {
        PreferenceDialog("Schedule day format", options, {}, {})
    }
}
