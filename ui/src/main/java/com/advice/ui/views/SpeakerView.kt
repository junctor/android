package com.advice.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme

@Composable
fun SpeakerView(name: String, title: String? = null, onSpeakerClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSpeakerClicked()
            }
            .padding(16.dp)
            .height(IntrinsicSize.Min), verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(Color.White)
        )

        Spacer(Modifier.width(4.dp))
        Column {
            Text(name)
            if (title?.isNotBlank() == true) {
                Text(title)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SpeakerViewPreview() {
    ScheduleTheme {
        SpeakerView("John McAfee", "Hacker", {})
    }
}

@Preview(showBackground = true)
@Composable
fun SpeakerViewNoTitlePreview() {
    ScheduleTheme {
        SpeakerView("Gary Johnson", null, {})
    }
}