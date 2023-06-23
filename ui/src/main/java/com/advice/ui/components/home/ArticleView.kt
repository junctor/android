package com.advice.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import java.text.SimpleDateFormat
import java.util.Date

@Composable
fun ArticleView(text: String, date: Date?) {
    HomeCard {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Text(text)
            if (date != null) {
                val formatter = SimpleDateFormat("MM/dd/yyyy")
                Text(
                    formatter.format(date),
                    modifier = Modifier.fillMaxWidth(),
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
