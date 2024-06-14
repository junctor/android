package com.advice.feedback.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun TextBoxItem(
    caption: String,
    value: String,
) {
    Column(Modifier.fillMaxWidth()) {
        Text(caption)
        TextField(
            value = value,
            onValueChange = {},
            placeholder = {
                Text("Enter your answer")
            },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
