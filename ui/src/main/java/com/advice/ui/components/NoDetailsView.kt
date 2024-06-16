package com.advice.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun NoDetailsView(text: String = "No further information available.\nMaybe ask Chatgpt.") {
    Text(
        text,
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        textAlign = TextAlign.Center,
    )
}

@PreviewLightDark
@Composable
private fun NoDetailsViewPreview() {
    ScheduleTheme {
        NoDetailsView()
    }
}
