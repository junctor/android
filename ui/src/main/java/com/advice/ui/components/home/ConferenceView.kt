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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.utils.TimeUtil
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.time.Instant

@Composable
internal fun ConferenceView(
    conference: Conference,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
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
                conference.name,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(Modifier.height(16.dp))
            Text(
                TimeUtil.getConferenceDateRange(context, conference = conference),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(conference.timezone, style = MaterialTheme.typography.bodyLarge)

            val tagline = conference.tagline
            if (tagline != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    tagline,
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun ConferenceViewPreview() {
    val conference = Conference.Zero

    ScheduleTheme {
        ConferenceView(conference)
    }
}
