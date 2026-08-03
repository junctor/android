package com.advice.ui.glitch

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.view.View
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.advice.ui.glitch.effect.GlitchFrame
import com.advice.ui.glitch.effect.GlitchRenderer
import com.advice.ui.glitch.effect.GlitchRng
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration.Companion.milliseconds

/**
 * Reusable glitch-effect image. Burst animation (chromatic aberration, horizontal
 * slice tears, corruption blocks) when [animated] is true; static source otherwise.
 */
@Composable
fun GlitchImage(
    bitmap: Bitmap,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    animated: Boolean = true,
    intensity: Float = 1f,
    enableScanlines: Boolean = false,
) {
    val inspectionMode = LocalInspectionMode.current
    if (!animated || inspectionMode || intensity <= 0f) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = contentDescription,
            contentScale = ContentScale.Fit,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    var isResumed by remember {
        mutableStateOf(lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
    }
    DisposableEffect(lifecycleOwner) {
        val observer =
            LifecycleEventObserver { _, _ ->
                isResumed = lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val rng = remember { GlitchRng() }
    var frame by remember { mutableStateOf(GlitchFrame.IDLE) }

    LaunchedEffect(isResumed, bitmap, intensity, enableScanlines) {
        if (!isResumed) {
            frame = GlitchFrame.IDLE
            return@LaunchedEffect
        }
        while (isActive) {
            delay(rng.nextIdleDelayMs().milliseconds)
            if (!isActive) break
            frame =
                rng.nextBurstFrame(
                    width = bitmap.width,
                    height = bitmap.height,
                    intensity = intensity,
                    enableScanlines = enableScanlines,
                )
            delay(rng.nextBurstDurationMs().milliseconds)
            frame = GlitchFrame.IDLE
        }
    }

    val renderer = remember { GlitchRenderer() }
    DisposableEffect(Unit) {
        onDispose { renderer.recycle() }
    }

    AndroidView(
        factory = { context ->
            GlitchSurfaceView(context).apply {
                this.renderer = renderer
                this.source = bitmap
                this.frame = frame
                contentDescription?.let { desc ->
                    this.contentDescription = desc
                    importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                }
            }
        },
        update = { view ->
            view.renderer = renderer
            view.source = bitmap
            view.frame = frame
            contentDescription?.let { desc ->
                view.contentDescription = desc
            }
            view.invalidate()
        },
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
fun rememberGlitchBitmap(drawableRes: Int): Bitmap {
    val context = LocalContext.current
    return remember(drawableRes, context.resources) {
        BitmapFactory.decodeResource(context.resources, drawableRes)
    }
}

private class GlitchSurfaceView(
    context: android.content.Context,
) : View(context) {
    var renderer: GlitchRenderer? = null
    var source: Bitmap? = null
    var frame: GlitchFrame = GlitchFrame.IDLE

    init {
        setBackgroundColor(android.graphics.Color.TRANSPARENT)
        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val src = source ?: return
        val glitchRenderer = renderer ?: return
        glitchRenderer.draw(canvas, src, frame, width, height)
    }
}
