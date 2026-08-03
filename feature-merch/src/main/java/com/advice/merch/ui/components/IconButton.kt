package com.advice.merch.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.advice.merch.R
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.theme.roundedCornerShape
import androidx.compose.material3.IconButton as MaterialIconButton

val iconButtonBackgroundColor = Color(0xFF333333)
val iconButtonForegroundColor = Color.White

@Composable
internal fun IconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String = stringResource(R.string.cd_options),
) {
    MaterialIconButton(
        onClick = onClick,
        modifier =
            modifier
                .background(iconButtonBackgroundColor, roundedCornerShape)
                .size(48.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconButtonForegroundColor,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
internal fun IconButton(
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = iconButtonBackgroundColor,
    contentDescription: String = stringResource(R.string.cd_options),
) {
    MaterialIconButton(
        onClick = onClick,
        modifier =
            modifier
                .background(backgroundColor, roundedCornerShape)
                .size(48.dp),
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription,
            tint = iconButtonForegroundColor,
            modifier = Modifier.size(18.dp),
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

@PreviewLightDark
@Composable
private fun IconButtonPainterPreview() {
    ScheduleTheme {
        Surface {
            IconButton(
                icon = painterResource(id = R.drawable.ic_delete),
                onClick = {},
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}
