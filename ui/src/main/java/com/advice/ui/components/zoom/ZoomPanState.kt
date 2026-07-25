package com.advice.ui.components.zoom

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Stable
internal class ZoomPanState(
    initialScale: Float = MIN_ZOOM,
    initialOffset: Offset = Offset.Zero,
) {
    var scale by mutableFloatStateOf(initialScale)
        private set
    var offset by mutableStateOf(initialOffset)
        private set

    var viewportSize by mutableStateOf(Size.Zero)
        private set
    var contentSize by mutableStateOf(Size.Zero)
        private set
    var maxZoom by mutableStateOf(DEFAULT_MAX_ZOOM)
        private set

    val isZoomed: Boolean by derivedStateOf { scale > MIN_ZOOM + 0.01f }

    private var animationJob: Job? = null

    fun updateLayout(
        viewport: Size,
        content: Size,
        maxZoom: Float = DEFAULT_MAX_ZOOM,
    ) {
        viewportSize = viewport
        contentSize = content
        this.maxZoom = maxZoom.coerceAtLeast(MIN_ZOOM)
    }

    fun snapToLayout() {
        scale = scale.coerceIn(MIN_ZOOM, maxZoom)
        offset = clampOffset(offset, scale, viewportSize, contentSize)
    }

    fun stopAnimations() {
        animationJob?.cancel()
        animationJob = null
    }

    /**
     * Synchronous transform update for use inside restricted pointer coroutines.
     */
    fun applyTransform(
        zoomChange: Float,
        panChange: Offset,
        centroid: Offset,
    ) {
        val oldScale = scale
        val newScale = (oldScale * zoomChange).coerceIn(MIN_ZOOM, maxZoom)
        val zoomed =
            if (zoomChange != 1f) {
                zoomAround(offset, oldScale, newScale, centroid)
            } else {
                offset
            }
        scale = newScale
        offset = clampOffset(zoomed + panChange, newScale, viewportSize, contentSize)
    }

    suspend fun animateDoubleTap(tap: Offset) {
        val targetScale =
            if (scale < DOUBLE_TAP_ZOOM - 0.1f) {
                DOUBLE_TAP_ZOOM.coerceAtMost(maxZoom)
            } else {
                MIN_ZOOM
            }
        animateZoomTo(targetScale, tap)
    }

    suspend fun animateZoomTo(
        targetScale: Float,
        centroid: Offset,
    ) {
        val startScale = scale
        val startOffset = offset
        val endScale = targetScale.coerceIn(MIN_ZOOM, maxZoom)
        val endOffset =
            clampOffset(
                zoomAround(startOffset, startScale, endScale, centroid),
                endScale,
                viewportSize,
                contentSize,
            )
        coroutineScope {
            animationJob =
                launch {
                    val scaleAnim = Animatable(startScale)
                    val offsetAnim = Animatable(startOffset, Offset.VectorConverter)
                    coroutineScope {
                        launch {
                            scaleAnim.animateTo(
                                endScale,
                                spring(stiffness = Spring.StiffnessMediumLow),
                            ) {
                                scale = value
                            }
                        }
                        launch {
                            offsetAnim.animateTo(
                                endOffset,
                                spring(stiffness = Spring.StiffnessMediumLow),
                            ) {
                                offset = value
                            }
                        }
                    }
                    scale = endScale
                    offset = endOffset
                }
            animationJob?.join()
        }
    }

    suspend fun fling(
        velocity: Offset,
        decay: DecayAnimationSpec<Offset>,
    ) {
        if (velocity == Offset.Zero) return
        if (viewportSize == Size.Zero || contentSize == Size.Zero) return

        val (lower, upper) = offsetBounds(scale, viewportSize, contentSize)
        // Degenerate bounds (content fits) — nothing to fling.
        if (lower == upper) return

        coroutineScope {
            animationJob =
                launch {
                    val anim = Animatable(offset, Offset.VectorConverter)
                    anim.updateBounds(lower, upper)
                    anim.animateDecay(velocity, decay) {
                        offset = value
                    }
                    offset = clampOffset(anim.value, scale, viewportSize, contentSize)
                }
            animationJob?.join()
        }
    }

    suspend fun reset() {
        animateZoomTo(
            targetScale = MIN_ZOOM,
            centroid =
                Offset(
                    x = viewportSize.width / 2f,
                    y = viewportSize.height / 2f,
                ),
        )
    }

    fun resetImmediate() {
        stopAnimations()
        scale = MIN_ZOOM
        offset = clampOffset(Offset.Zero, MIN_ZOOM, viewportSize, contentSize)
    }
}

@Composable
internal fun rememberZoomPanState(): ZoomPanState = remember { ZoomPanState() }
