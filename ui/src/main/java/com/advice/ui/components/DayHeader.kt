package com.advice.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun DayHeader(day: String) {
    Label(day, modifier = Modifier.fillMaxWidth())
}

@PreviewLightDark
@Composable
private fun DayHeaderViewPreview() {
    ScheduleTheme {
        DayHeader("Wednesday")
    }
}
