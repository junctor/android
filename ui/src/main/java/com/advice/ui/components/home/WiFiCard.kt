package com.advice.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme


@Composable
internal fun WiFiCard(onConnectClicked: () -> Unit) {
    HomeCard {
        Column(Modifier.padding(16.dp)) {
            Text("Automatically connect to the DEF CON WiFi network when in range.")

            Button(onClick = onConnectClicked) {
                Text("Add WiFi Network")
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun WiFiCardPreview() {
    ScheduleTheme {
        WiFiCard({})
    }
}
