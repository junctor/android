package com.advice.locations.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun LocationRow(
    date: String,
    time: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()

    ) {
        Text(date, fontWeight = FontWeight.Black)
        Text(time)
    }
}

@LightDarkPreview
@Composable
private fun LocationBottomSheetPreview() {
    ScheduleTheme {
        LocationRow(
            "Friday, August 10 - Saturday, August 11",
            "5:00 pm to 3:00 am",
        )
    }
}
