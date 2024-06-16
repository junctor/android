package com.advice.feedback.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
internal fun TextBoxItem(
    caption: String,
    value: String,
) {
    var value by remember { mutableStateOf(value) }

    Column(Modifier.fillMaxWidth()) {
        Text(caption)
        TextField(
            value = value,
            onValueChange = {
                value = it
            },
            placeholder = {
                Text("Enter your answer")
            },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 10,
        )
    }
}

@PreviewLightDark
@Composable
private fun TextBoxItemPreview() {
    ScheduleTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            TextBoxItem(
                caption = "Anything else we should know before you go?",
                value = "",
            )
            TextBoxItem(
                caption = "Anything else we should know before you go?",
                value = "I'm not sure",
            )
        }
    }
}
