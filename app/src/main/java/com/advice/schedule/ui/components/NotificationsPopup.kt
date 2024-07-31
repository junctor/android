package com.advice.schedule.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
internal fun NotificationsPopup(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .width(300.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier
                .padding(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 16.dp)
            ) {
                Text(
                    text = "Event Notifications",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onDismiss,
                ) {
                    Icon(
                        Icons.Default.Close, "Dismiss",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
            ) {
                val text = buildAnnotatedString {
                    append("Hacker Tracker can send you a notification ")
                    withStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                        )
                    ) {
                        append("20 mins")
                    }
                    append(" before an event starts.")
                }

                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (hasPermission) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text("Neat!")
                    }
                } else {
                    Button(
                        onClick = onRequestPermission,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text("Request Permission")
                    }
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun NotificationsPopupPreview() {
    ScheduleTheme {
        Surface {
            NotificationsPopup(
                hasPermission = false,
                onRequestPermission = {},
                onDismiss = {},
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ReminderPopupPreview() {
    ScheduleTheme {
        Surface {
            NotificationsPopup(
                hasPermission = true,
                onRequestPermission = {},
                onDismiss = {},
            )
        }
    }
}
