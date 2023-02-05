package com.advice.feature_ui.views

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

@Composable
fun EventRowView() {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .background(Color.White)
        .fillMaxWidth()
        .height(IntrinsicSize.Min)) {
        // Category
        Box(
            modifier = Modifier
                .width(4.dp)
                .padding(vertical = 4.dp)
                .fillMaxHeight()
                .clip(RectangleShape)
                .background(Color.Red)
        )

        Spacer(modifier = Modifier.width(85.dp))
        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
            Text("Compelled Decryption", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Track 1", style = MaterialTheme.typography.bodyMedium)
            CategoryView()
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
fun CategoryView() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(Color.Red)
        )
        Spacer(Modifier.width(8.dp))
        Text("Event Category", style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Composable
fun EventRowViewPreview() {
    MaterialTheme {
        EventRowView()
    }
}