package com.advice.products.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape

val iconButtonBackgroundColor = Color(0xFF333333)
val iconButtonForegroundColor = Color.White

@Composable
internal fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(iconButtonBackgroundColor, roundedCornerShape)
            .size(32.dp)
            .clickable { onClick() },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Options",
            tint = iconButtonForegroundColor,
            modifier = Modifier
                .size(14.dp)
                .align(Alignment.Center),
        )
    }
}

@PreviewLightDark
@Composable
private fun IconButtonPreview() {
    ScheduleTheme {
        Surface {
            IconButton(
                icon = Icons.Default.MoreVert,
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
