package com.advice.feedback.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

@Composable
fun DiscardPopup(
    modifier: Modifier = Modifier,
    onDiscard: () -> Unit = {},
    onCancel: () -> Unit = {},
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
                    text = "Discard Feedback",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                )
                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onCancel,
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
                Text(
                    text = "Are you sure you wish to discard your feedback?",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onDiscard,
                    modifier = Modifier.fillMaxWidth(),
                    shape = roundedCornerShape,
                ) {
                    Text("Discard")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onCancel,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.onSurface, roundedCornerShape),
                    shape = roundedCornerShape,
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun DiscardPopupPreview() {
    ScheduleTheme {
        DiscardPopup()
    }
}
