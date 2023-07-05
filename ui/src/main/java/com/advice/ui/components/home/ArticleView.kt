package com.advice.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.components.Paragraph
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ArticleView(text: String, date: Date?) {
    HomeCard {
        Column{
            Paragraph(text)
            if (date != null) {
                val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                Text(
                    formatter.format(date),
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
fun ArticleViewPreview() {
    ScheduleTheme {
        ArticleView("Welcome to DEFCON 28!", Date())
    }
}
