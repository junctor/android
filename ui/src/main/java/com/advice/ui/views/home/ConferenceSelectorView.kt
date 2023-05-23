package com.advice.ui.views

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.advice.core.utils.Time
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.util.*

@Composable
fun ConferenceSelectorView(
    label: String,
    startDate: Date,
    endDate: Date,
    isFinished: Boolean,
    modifier: Modifier = Modifier,
    onConferenceClick: () -> Unit,
) {
    DropdownMenuItem(
        text = {
            val alpha = if (isFinished) 0.6f else 1.0f
            Column(
                Modifier
                    .padding(16.dp)
                    .alpha(alpha)) {
                Text(label, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.labelLarge)
                Text("$startDate - $endDate")
            }
        },
        onClick = onConferenceClick,
        trailingIcon = {
            Icon(Icons.Default.ArrowDropDown, null)
        },
        modifier = modifier.fillMaxWidth()
    )
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