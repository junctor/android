package com.advice.ui.utils

import android.app.Activity
import android.content.ContextWrapper
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.children
import androidx.core.view.insets.ColorProtection

/**
 * Temporarily tints status and navigation bar colors, restoring prior values on dispose.
 *
 * Uses window bar colors directly instead of [androidx.activity.enableEdgeToEdge]. Re-entering
 * edge-to-edge with a non-transparent `SystemBarStyle.dark` scrim installs an API 35+
 * [androidx.core.view.insets.ProtectionLayout] that can leave the system navigation bar
 * persistently protected and overlapping Schedule FABs after the screen is dismissed.
 */
@Composable
fun TemporarySystemBarScrims(scrim: Color) {
    val view = LocalView.current
    val scrimArgb = scrim.toArgb()
    DisposableEffect(view, scrimArgb) {
        val window =
            generateSequence(view.context) { (it as? ContextWrapper)?.baseContext }
                .filterIsInstance<Activity>()
                .firstOrNull()
                ?.window
                ?: return@DisposableEffect onDispose {}

        @Suppress("DEPRECATION")
        val previousStatusBarColor = window.statusBarColor

        @Suppress("DEPRECATION")
        val previousNavigationBarColor = window.navigationBarColor

        @Suppress("DEPRECATION")
        window.statusBarColor = scrimArgb
        @Suppress("DEPRECATION")
        window.navigationBarColor = scrimArgb

        onDispose {
            @Suppress("DEPRECATION")
            window.statusBarColor = previousStatusBarColor
            @Suppress("DEPRECATION")
            window.navigationBarColor = previousNavigationBarColor
            clearEdgeToEdgeProtections(window.decorView)
        }
    }
}

/**
 * Drops API 35+ edge-to-edge [ColorProtection] overlays so system navigation returns to the
 * activity's default transparent edge-to-edge style (e.g. after a prior scrim via enableEdgeToEdge).
 */
@Composable
fun ClearEdgeToEdgeProtectionsEffect() {
    val view = LocalView.current
    DisposableEffect(view) {
        clearEdgeToEdgeProtections(view.rootView)
        onDispose {}
    }
}

internal fun clearEdgeToEdgeProtections(root: View?) {
    if (Build.VERSION.SDK_INT < 35) return
    val decorView = root as? ViewGroup ?: return
    val protectionViews =
        decorView.children
            .filter { child ->
                val tag = child.tag
                tag is List<*> && tag.size == 4 && tag.firstOrNull() is ColorProtection
            }.toList()
    protectionViews.forEach { decorView.removeView(it) }
}
