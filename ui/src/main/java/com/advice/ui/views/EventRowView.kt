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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.schedule.models.firebase.FirebaseTag

@Composable
fun EventRowView(title: String, location: String, tags: List<FirebaseTag>, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        // Category
        Box(
            modifier = Modifier
                .width(4.dp)
                .padding(vertical = 4.dp)
                .fillMaxHeight()
                .clip(RectangleShape)
                .background(Color(android.graphics.Color.parseColor(tags.first().color)))
        )

        Spacer(modifier = Modifier.width(85.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(location, style = MaterialTheme.typography.bodyMedium)
            Row {
                for (tag in tags) {
                    CategoryView(tag)
                }
            }
        }
        Icon(Icons.Default.Star, contentDescription = null,
            Modifier
                .size(48.dp)
                .padding(12.dp)
                .clickable {

                })
    }
}

@Composable
fun CategoryView(tag: FirebaseTag) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color(android.graphics.Color.parseColor(tag.color)))
        )
        Spacer(Modifier.width(8.dp))
        Text(tag.label, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun EventRowViewPreview() {
    MaterialTheme {
        EventRowView(title = "Compelled Decryption", location = "Track 1", tags = listOf(FirebaseTag(label = "Talk", color_background = "#FF61EEAA")))
    }
}

fun Color.fromHex(color: String) = Color(android.graphics.Color.parseColor("#$color"))
