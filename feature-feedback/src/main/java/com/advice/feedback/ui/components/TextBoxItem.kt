package com.advice.feedback.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
internal fun TextBoxItem(
    caption: String,
    value: String,
) {
    var value by remember { mutableStateOf("") }

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
