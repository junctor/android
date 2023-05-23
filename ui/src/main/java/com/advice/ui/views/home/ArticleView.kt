package com.advice.ui.views.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun ArticleView(text: String) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(text, modifier = Modifier.padding(16.dp))
    }
}

@LightDarkPreview
@Composable
fun ArticleViewPreview() {
    ScheduleTheme {
        ArticleView("Welcome to DEFCON 28!")
    }
}
