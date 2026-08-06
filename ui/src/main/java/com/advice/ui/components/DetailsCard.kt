package com.advice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
internal fun DetailsCard(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val isClickable = onClick != null
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier =
                Modifier
                    .clickable(enabled = isClickable, onClick = onClick ?: {})
                    .padding(16.dp),
        ) {
            Icon(icon, null)
            Spacer(Modifier.width(12.dp))
            Text(text, modifier = Modifier.weight(1f))
        }
    }
}
