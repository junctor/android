package com.advice.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.core.utils.TimeUtil
import com.advice.ui.components.Paragraph
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArticleView(title: String, text: String, date: Date?, onDismiss: (() -> Unit)? = null) {
    HomeCard {
        Column(horizontalAlignment = Alignment.End) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    title,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp)
                        .weight(1f),
                    fontWeight = FontWeight.Black,
                )
                if (onDismiss != null) {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Dismiss")
                    }
                }
            }

            Paragraph(text, modifier = Modifier.fillMaxWidth())
            if (date != null) {
                Text(
                    text = TimeUtil.getNewsTimestamp(date),
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun ArticleViewPreview() {
    ScheduleTheme {
        ArticleView(
            title = "DEFCON 28",
            text = "Welcome to DEFCON 28!",
            date = Date(),
            onDismiss = {}
        )
    }
}
