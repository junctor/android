package com.advice.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.advice.ui.R
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun BackButton(
    onClick: () -> Unit,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    tint: Color = LocalContentColor.current,
) {
    IconButton(
        onClick = onClick,
        colors = colors,
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_back),
            contentDescription = "back",
            tint = tint,
        )
    }
}

@PreviewLightDark
@Composable
private fun BackButtonPreview() {
    ScheduleTheme {
        Surface {
            BackButton(
                onClick = {},
            )
        }
    }
}
