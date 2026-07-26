package com.advice.ui.utils

import android.content.ContextWrapper
import android.graphics.Color as AndroidColor
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView

/**
 * Temporarily applies dark system-bar scrims via [SystemBarStyle], restoring the app's
 * default transparent edge-to-edge styles on dispose.
 */
@Composable
fun TemporarySystemBarScrims(scrim: Color) {
    val view = LocalView.current
    val scrimArgb = scrim.toArgb()
    DisposableEffect(view, scrimArgb) {
        val activity =
            generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
                .filterIsInstance<ComponentActivity>()
                .firstOrNull()
                ?: return@DisposableEffect onDispose {}

        activity.enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(scrimArgb),
            navigationBarStyle = SystemBarStyle.dark(scrimArgb),
        )

        onDispose {
            activity.enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
                navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            )
        }
    }
}
