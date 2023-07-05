package com.advice.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun ConferenceView(
    name: String,
    date: String,
    location: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 140.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(name, textAlign = TextAlign.Center, style = MaterialTheme.typography.headlineLarge)
            Text(date, style = MaterialTheme.typography.bodyMedium)
            Text(location, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@LightDarkPreview
@Composable
private fun ConferenceViewPreview() {
    ScheduleTheme {
        ConferenceView(
            "DEFCON 30", "August 4th - 7th, 2022",
            "Las Vegas, NV",
            "Welcome to DEF CON - the largest underground hacking conference in the world.",
        )
    }
}
