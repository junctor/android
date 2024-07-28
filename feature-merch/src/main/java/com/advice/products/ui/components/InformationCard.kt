package com.advice.products.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

data class DismissibleInformation(
    val key: String,
    val text: String,
    val document: Long?,
)

@Composable
fun InformationCard(
    information: DismissibleInformation,
    onLearnMore: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(4.dp),
        color = MaterialTheme.colorScheme.inverseSurface,
        shadowElevation = 12.dp,
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Outlined.Info, contentDescription = "info")
                Text(
                    text = information.text,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Black
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            if (information.document != null) {
                Button(
                    onClick = onLearnMore,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text("Learn More", color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
private fun InformationCardPreview() {
    ScheduleTheme {
        Column {
            InformationCard(
                information = DismissibleInformation(
                    key = "key",
                    text = "This message has a document for more details.",
                    document = 1,
                ),
                onLearnMore = {},
                onDismiss = {},
                modifier = Modifier.padding(16.dp),
            )
            InformationCard(
                information = DismissibleInformation(
                    key = "key",
                    text = "Information message without a document for more details.",
                    document = null,
                ),
                onLearnMore = {},
                onDismiss = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
