package com.advice.ui.components

import androidx.compose.foundation.background
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
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

data class ThemeOption(val title: String, val theme: String)

@Composable
fun ButtonPreference(onPreferenceChanged: (String) -> Unit) {
    val title = "Choose theme"
    val options = listOf(
        ThemeOption("Light", "light"),
        ThemeOption("Dark", "dark"),
        ThemeOption("System default", "system")
    )

    var dialogOpen by remember { mutableStateOf(false) }

    if (dialogOpen) {
        Dialog(
            title = title,
            options = options,
            onOptionSelected = {
                onPreferenceChanged(it)
                dialogOpen = false
            },
            onDismiss = {
                dialogOpen = false
            }
        )
    }

    Row(Modifier
        .clickable {
            dialogOpen = true
        }
        .fillMaxWidth()
        .height(IntrinsicSize.Min)
        .padding(16.dp)) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title)
        }

    }
}

@Composable
private fun Dialog(
    title: String,
    options: List<ThemeOption>,
    onOptionSelected: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                for (option in options) {
                    TextButton(onClick = { onOptionSelected(option.theme) }) {
                        Text(option.title, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        },
        confirmButton = { },
        tonalElevation = 0.dp,
    )
}

@LightDarkPreview
@Composable
private fun ButtonPreferencePreview() {
    ScheduleTheme {
        ButtonPreference({})
    }
}

@LightDarkPreview
@Composable
private fun ButtonPreferenceDarkPreview() {
    val title = "Choose theme"
    val options = listOf(
        ThemeOption("Light", "light"),
        ThemeOption("Dark", "dark"),
        ThemeOption("System default", "system")
    )

    ScheduleTheme {
        Dialog(title, options, {}, {})
    }
}