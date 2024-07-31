package com.advice.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
) {
    EmptyMessage(
        message = message,
        modifier = modifier,
        title = "Error",
    )
}

@Composable
fun EmptyMessage(
    message: String,
    modifier: Modifier = Modifier,
    title: String? = null,
) {
    Box(modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.align(Alignment.Center),
        ) {
            if (title != null) {
                Text(
                    title,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                )
            }
            Text(
                message,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun EmptyViewPreview() {
    ScheduleTheme {
        Surface {
            EmptyMessage(
                message = "No maps for DEF CON 31",
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ErrorViewPreview() {
    ScheduleTheme {
        Surface {
            ErrorMessage("Could not fetch data")
        }
    }
}

