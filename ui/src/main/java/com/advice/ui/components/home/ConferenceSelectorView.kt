package com.advice.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.core.local.Conference
import com.advice.core.utils.TimeUtil
import com.advice.ui.R
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ConferenceSelectorView(
    conference: Conference,
    modifier: Modifier = Modifier,
    onConferenceClick: () -> Unit,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .clickable { onConferenceClick() }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val alpha = if (conference.hasFinished) 0.6f else 1.0f
        Column(
            Modifier
                .weight(1f)
                .padding(16.dp)
                .alpha(alpha)
        ) {
            Text(
                conference.name,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Black,
            )
            Text(
                TimeUtil.getConferenceDateRange(context, conference),
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Icon(
            painterResource(R.drawable.arrow_back),
            null,
            modifier = Modifier
                .padding(16.dp)
                .size(12.dp)
                .rotate(180f)
        )
    }
}

@LightDarkPreview
@Composable
private fun ConferenceSelectorViewPreview() {
    ScheduleTheme {
        ConferenceSelectorView(
            conference = Conference.Zero,
            onConferenceClick = {}
        )
    }
}
