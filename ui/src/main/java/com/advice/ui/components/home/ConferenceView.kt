package com.advice.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
internal fun ConferenceView(
    name: String,
    startDate: Date,
    endDate: Date,
    timezone: String,
    description: String?,
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                name,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(16.dp))
            Text(
                getConferenceDateRange(startDate, endDate),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(timezone, style = MaterialTheme.typography.bodyLarge)
            if (description != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    description,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun getConferenceDateRange(startDate: Date, endDate: Date): String {
    val startFormat = SimpleDateFormat("MMMM d")
    val endFormat = SimpleDateFormat("MMMM d, yyyy")
    return "${startFormat.format(startDate)} - ${endFormat.format(endDate)}"
}

@LightDarkPreview
@Composable
private fun ConferenceViewPreview() {
    ScheduleTheme {
        ConferenceView(
            name = "DEF CON 30",
            startDate = Date(),
            endDate = Date(),
            timezone = "America/Los_Angeles",
            description = "Welcome to DEF CON - the largest underground hacking conference in the world.",
        )
    }
}
