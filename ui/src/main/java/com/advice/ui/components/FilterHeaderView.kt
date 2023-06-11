package com.advice.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme

@Composable
fun FilterHeaderView(title: String) {
    Text(title, modifier = Modifier.padding(16.dp))
}

@Preview(showBackground = true)
@Composable
fun FilterHeaderViewPreview() {
    ScheduleTheme {
        FilterHeaderView("General")
    }
}