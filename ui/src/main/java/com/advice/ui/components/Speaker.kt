package com.advice.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
fun Speaker(
    name: String,
    title: String?,
    onSpeakerClicked: (() -> Unit),
    modifier: Modifier = Modifier,
) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onSpeakerClicked()
                }
                .padding(16.dp)
                .height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name)
                if (title?.isNotBlank() == true) {
                    Text(title)
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun SpeakerPreview() {
    ScheduleTheme {
        Column {
            Speaker(
                name = "John McAfee",
                title = "CEO",
                onSpeakerClicked = {}
            )
            Speaker(
                name = "Gary Johnson",
                title = null,
                onSpeakerClicked = {}
            )
        }
    }
}