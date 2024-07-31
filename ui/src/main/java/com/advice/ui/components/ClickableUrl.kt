package com.advice.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@Composable
fun ClickableUrl(
    label: String,
    url: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
        shape = roundedCornerShape,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
            Modifier
                .clickable(enabled = enabled, onClick = onClick)
                .padding(16.dp),
        ) {
            Column(Modifier.fillMaxWidth()) {
                Text(label)
                Text(url, color = MaterialTheme.colorScheme.primary, maxLines = 1)
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun ClickableUrlPreview() {
    ScheduleTheme {
        ClickableUrl("Discord", "https://www.discord.com", onClick = {})
    }
}
