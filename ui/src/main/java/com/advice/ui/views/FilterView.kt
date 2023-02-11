package com.advice.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.schedule.models.firebase.FirebaseTag

@Composable
fun FilterView(tag: FirebaseTag) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .fillMaxWidth()
        .clickable {
            // todo:
        }
        .padding(16.dp)) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(tag.color)))
        )

        Spacer(Modifier.width(12.dp))

        Text(tag.label)
    }
}

@Preview(showBackground = true)
@Composable
fun FilterViewPreview() {
    MaterialTheme {
        FilterView(FirebaseTag(label = "Talk", color_background = "#123123"))
    }
}