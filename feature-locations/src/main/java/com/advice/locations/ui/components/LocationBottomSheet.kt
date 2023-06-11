package com.advice.locations.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun LocationBottomSheet(title: String, schedule: List<String>, modifier: Modifier = Modifier, onScheduleClicked: () -> Unit, onDismiss: () -> Unit) {
    LazyColumn(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        item {
            Text(title, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface)
        }
        items(schedule) {
            Text(it, Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurface)
        }
        item {
            Button(onClick = onScheduleClicked, Modifier.fillMaxWidth()) {
                Text("Open Schedule")
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun LocationBottomSheetPreview() {
    ScheduleTheme {
        LocationBottomSheet("Skybridge Entrance", listOf("August 12th 5:00 pm to August 13th 3:00 am"), Modifier, {}, {})
    }
}