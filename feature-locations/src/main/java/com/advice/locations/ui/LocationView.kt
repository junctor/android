package com.advice.locations.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Location
import com.advice.core.local.LocationStatus
import com.advice.locations.ui.preview.LocationProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme


@Composable
fun LocationView(
    label: String,
    description: String?,
    status: LocationStatus,
    hasChildren: Boolean,
    isExpanded: Boolean,
    depth: Int,
    onScheduleClicked: () -> Unit
) {
    val colour = when (status) {
        LocationStatus.Closed -> Color.Red
        LocationStatus.Mixed -> Color.Yellow
        LocationStatus.Open -> Color.Green
        LocationStatus.Unknown -> Color.Gray
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
        .clickable {
            onScheduleClicked()
        }
        .padding(horizontal = 16.dp, vertical = 8.dp)) {
        Spacer(modifier = Modifier.width((16 * depth).dp))
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(colour)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center) {
            Text(label)
            if (description != null) {
                Text(description)
            }
        }

        if (hasChildren) {
            val rotation = if (isExpanded) 180f else 0f
            IconButton(onClick = onScheduleClicked) {
                Icon(Icons.Default.KeyboardArrowDown, null, modifier = Modifier.rotate(rotation))
            }
        }
    }
}

@LightDarkPreview
@Composable
fun LocationViewPreview(
    @PreviewParameter(LocationProvider::class) location: Location,
) {
    ScheduleTheme {
        LocationView(
            location.name,
            location.shortName,
            LocationStatus.Open,
            hasChildren = true,
            isExpanded = true,
            location.depth
        ) {}
    }
}