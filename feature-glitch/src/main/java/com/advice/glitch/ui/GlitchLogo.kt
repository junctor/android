package com.advice.glitch.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.advice.core.storage.UserPreferencesStore
import com.advice.glitch.R

@Composable
fun GlitchLogo(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    animated: Boolean = true,
    intensity: Float = 1f,
) {
    val animationEnabled = rememberGlitchAnimationEnabled()
    val shouldAnimate = animated && animationEnabled

    if (!shouldAnimate) {
        Image(
            painter = painterResource(id = R.drawable.logo_glitch),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val bitmap = rememberGlitchBitmap(R.drawable.logo_clean)
    GlitchImage(
        bitmap = bitmap,
        contentDescription = contentDescription,
        animated = true,
        intensity = intensity,
        enableScanlines = false,
        modifier = modifier,
    )
}

@Composable
private fun rememberGlitchAnimationEnabled(): Boolean {
    val inspectionMode = LocalInspectionMode.current
    if (inspectionMode) {
        return true
    }

    val context = LocalContext.current
    val preferences =
        remember(context) {
            context.getSharedPreferences(UserPreferencesStore.KEY_PREFERENCES, Context.MODE_PRIVATE)
        }
    var enabled by remember {
        mutableStateOf(preferences.getBoolean(UserPreferencesStore.GLITCH_ANIMATION_ENABLED_KEY, true))
    }

    DisposableEffect(preferences) {
        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                if (key == UserPreferencesStore.GLITCH_ANIMATION_ENABLED_KEY) {
                    enabled = prefs.getBoolean(key, true)
                }
            }
        preferences.registerOnSharedPreferenceChangeListener(listener)
        onDispose {
            preferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }

    return enabled
}

@PreviewLightDark
@Composable
private fun GlitchLogoPreview() {
    GlitchLogo(animated = false)
}
