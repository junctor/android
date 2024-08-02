package com.advice.ui.components.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun PopupContainer(
    onDismiss: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .zIndex(10F),
        contentAlignment = Alignment.Center,
    ) {
        Popup(
            alignment = Alignment.Center,
            properties = PopupProperties(
                excludeFromSystemGesture = true,
            ),
            onDismissRequest = onDismiss,
        ) {
            content()
        }
    }
}

@PreviewLightDark
@Composable
private fun PreviewPopupContainer() {
    ScheduleTheme {
        PopupContainer {
            NotificationsPopup(
                hasPermission = false,
                onRequestPermission = {},
                onDismiss = {},
            )
        }
    }
}
