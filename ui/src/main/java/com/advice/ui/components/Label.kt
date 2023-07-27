package com.advice.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun Label(text: String, modifier: Modifier = Modifier) {
    Text(
        text,
        modifier = modifier
            .padding(16.dp),
        textAlign = TextAlign.Center,
    )
}

@LightDarkPreview
@Composable
private fun LabelPreview() {
    ScheduleTheme {
        Label("General")
    }
}