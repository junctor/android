package com.advice.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.R

@Composable
fun EmptyView(message: String? = null, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                "404",
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.displayLarge
            )
            if (message != null) {
                Text(
                    message,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
        Image(
            painterResource(R.drawable.skull),
            null,
            Modifier
                .padding(bottom = 16.dp)
                .size(48.dp)
                .align(Alignment.BottomCenter),
        )
    }
}

@LightDarkPreview
@Composable
fun EmptyViewPreview() {
    ScheduleTheme {
        EmptyView("Maps not found")
    }
}