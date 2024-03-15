package com.advice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun SwitchPreference(
    title: String,
    isChecked: Boolean,
    summary: String? = null,
    summaryOn: String? = null,
    summaryOff: String? = null,
    onPreferenceChanged: (Boolean) -> Unit
) {
    var checked by rememberSaveable {
        mutableStateOf(isChecked)
    }

    Row(
        Modifier
            .clickable {
                checked = !checked
                onPreferenceChanged(checked)
            }
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(16.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title)
            when {
                summary != null -> {
                    Text(summary)
                }

                summaryOn != null && summaryOff != null -> {
                    Text(if (checked) summaryOn else summaryOff)
                }
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                onPreferenceChanged(it)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                uncheckedThumbColor = Color.White
            )
        )
    }
}

@LightDarkPreview
@Composable
private fun SwitchPreferencePreview() {
    ScheduleTheme {
        Column {
            SwitchPreference(
                "Events in (EUROPE/HELSINKI)",
                true,
                summaryOn = "Using conference's timezone",
                summaryOff = "Using device's timezone"
            ) {
            }
            SwitchPreference("Show filter button", false) {}
        }
    }
}
