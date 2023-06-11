package com.advice.ui.components.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.R

@Composable
fun ConferenceView(name: String) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 140.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Image(
                painterResource(R.drawable.skull),
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
            Text(name)
        }
    }
}

@LightDarkPreview
@Composable
fun ConferenceViewPreview() {
    ScheduleTheme {
        ConferenceView("DEFCON")
    }
}
