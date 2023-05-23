package com.advice.ui.views.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun CountdownView(time: Long) {
    val seconds: Long = time / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "$days days".uppercase(),
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                "${hours % 24} hours".uppercase(),
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                "${minutes % 60} minutes".uppercase(),
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displaySmall
            )
            Text(
                "${seconds % 60} seconds".uppercase(),
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displaySmall
            )
        }
    }
}

@LightDarkPreview
@Composable
fun CountdownViewPreview() {
    ScheduleTheme {
        CountdownView(152_352_123)
    }
}