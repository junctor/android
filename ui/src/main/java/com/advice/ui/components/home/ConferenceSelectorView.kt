package com.advice.ui.components

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.core.utils.Time
import com.advice.ui.R
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ConferenceSelectorView(
    label: String,
    startDate: Date,
    endDate: Date,
    isFinished: Boolean,
    modifier: Modifier = Modifier,
    onConferenceClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("MMMM d")
    val yearFormat = SimpleDateFormat("yyyy")

    Row(
        modifier = modifier
            .clickable { onConferenceClick() }
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val alpha = if (isFinished) 0.6f else 1.0f
        Column(
            Modifier
                .weight(1f)
                .padding(16.dp)
                .alpha(alpha)
        ) {
            Text(
                label,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                "${dateFormat.format(startDate)} - ${dateFormat.format(endDate)}, ${
                    yearFormat.format(
                        startDate
                    )
                }",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Icon(
            painterResource(R.drawable.baseline_arrow_back_ios_new_24),
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
fun ConferenceDropdownPreview() {
    ScheduleTheme {
        ConferenceSelectorView(
            label = "Disobey 2023",
            Time.now(),
            Time.now(),
            isFinished = false
        ) {

        }
    }
}