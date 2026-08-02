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
import kotlin.math.abs

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
    var maxZoom by mutableFloatStateOf(DEFAULT_MAX_ZOOM)
        private set

    /**
     * When false, zoom/settle snaps immediately (reduced-motion / animator scale 0).
     */
    var animationsEnabled: Boolean = true

    val isZoomed: Boolean by derivedStateOf { scale > MIN_ZOOM + 0.01f }

    /** True when offset sits at the left hard bound (room for previous-page handoff). */
    val atLeftEdge: Boolean by derivedStateOf {
        val (lower, upper) = offsetBounds(scale, viewportSize, contentSize)
        abs(upper.x - lower.x) < HORIZONTAL_EDGE_EPSILON ||
            offset.x >= upper.x - HORIZONTAL_EDGE_EPSILON
    }

    /** True when offset sits at the right hard bound (room for next-page handoff). */
    val atRightEdge: Boolean by derivedStateOf {
        val (lower, upper) = offsetBounds(scale, viewportSize, contentSize)
        abs(upper.x - lower.x) < HORIZONTAL_EDGE_EPSILON ||
            offset.x <= lower.x + HORIZONTAL_EDGE_EPSILON
    }

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
     * Whether this state can still absorb a horizontal pan of [deltaX].
     * When false at an edge, excess pan should be left for a parent pager.
     */
    fun canPanHorizontally(deltaX: Float): Boolean =
        canPanHorizontally(
            offset = offset,
            scale = scale,
            viewport = viewportSize,
            content = contentSize,
            deltaX = deltaX,
        )

    /**
     * Synchronous transform update for use inside restricted pointer coroutines.
     *
     * When [overscroll] is true, scale/offset may briefly exceed hard limits.
     * When [consumeHorizontal] is false, horizontal pan is ignored (edge handoff).
     */
    fun applyTransform(
        zoomChange: Float,
        panChange: Offset,
        centroid: Offset,
        overscroll: Boolean = true,
        consumeHorizontal: Boolean = true,
    ) {
        val oldScale = scale
        // At hard zoom limits, further pinch must not nudge scale via zoomAround or
        // apply the accompanying centroid pan — otherwise the map drifts while the
        // user tries to zoom past max/min. Single-finger pan (zoomChange == 1) still works.
        val ignorePinchZoom =
            (oldScale >= maxZoom && zoomChange > 1f) ||
                (oldScale <= MIN_ZOOM && zoomChange < 1f)
        val effectiveZoomChange = if (ignorePinchZoom) 1f else zoomChange
        val effectivePanChange = if (ignorePinchZoom) Offset.Zero else panChange
        val scaleRange =
            if (overscroll) {
                overscrollScaleRange(maxZoom)
            } else {
                MIN_ZOOM..maxZoom
            }
        val newScale =
            (oldScale * effectiveZoomChange).coerceIn(scaleRange.start, scaleRange.endInclusive)
        val zoomed =
            if (kotlin.math.abs(newScale - oldScale) > 0.0001f) {
                zoomAround(offset, oldScale, newScale, centroid)
            } else {
                offset
            }
        val pan =
            if (consumeHorizontal) {
                effectivePanChange
            } else {
                Offset(0f, effectivePanChange.y)
            }
        val proposed = zoomed + pan
        scale = newScale
        offset =
            if (overscroll) {
                // Outward horizontal rubber-band is suppressed at edges so a parent
                // pager can take the unconsumed pan (gallery-style handoff).
                softClampWithEdgeHandoff(proposed, newScale)
            } else {
                clampOffset(proposed, newScale, viewportSize, contentSize)
            }
    }

    /**
     * Soft-clamps [proposed], but does not rubber-band past horizontal edges
     * when the pan would move further outward — those stay at the hard bound.
     */
    private fun softClampWithEdgeHandoff(
        proposed: Offset,
        newScale: Float,
    ): Offset {
        val soft = softClampOffset(proposed, newScale, viewportSize, contentSize)
        val (lower, upper) = offsetBounds(newScale, viewportSize, contentSize)
        val hardX =
            when {
                proposed.x > upper.x -> upper.x
                proposed.x < lower.x -> lower.x
                else -> soft.x
            }
        return Offset(hardX, soft.y)
    }

    fun isOverscrolled(): Boolean =
        isScaleOverscrolled(scale, maxZoom) ||
            isOffsetOverscrolled(offset, scale, viewportSize, contentSize)

    suspend fun settleAfterGesture() {
        val endScale = scale.coerceIn(MIN_ZOOM, maxZoom)
        val endOffset = clampOffset(offset, endScale, viewportSize, contentSize)
        if (abs(endScale - scale) < 0.001f &&
            abs(endOffset.x - offset.x) < 0.5f &&
            abs(endOffset.y - offset.y) < 0.5f
        ) {
            scale = endScale
            offset = endOffset
            return
        }
        animateTo(endScale, endOffset)
    }

    suspend fun animateDoubleTap(tap: Offset) {
        val targetScale = nextDoubleTapScale(scale, maxZoom)
        animateZoomTo(targetScale, tap)
    }

    suspend fun animateZoomByStep(
        step: Float,
        centroid: Offset = viewportCenter(),
    ) {
        stopAnimations()
        animateZoomTo(scale * step, centroid)
    }

    suspend fun animateZoomTo(
        targetScale: Float,
        centroid: Offset,
    ) {
        stopAnimations()
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
        animateTo(endScale, endOffset, startScale, startOffset)
    }

    private suspend fun animateTo(
        endScale: Float,
        endOffset: Offset,
        startScale: Float = scale,
        startOffset: Offset = offset,
    ) {
        stopAnimations()
        if (!animationsEnabled) {
            scale = endScale
            offset = endOffset
            return
        }
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
        if (isOverscrolled()) {
            settleAfterGesture()
            return
        }

        val (lower, upper) = offsetBounds(scale, viewportSize, contentSize)
        // Degenerate bounds (content fits) — nothing to fling.
        if (lower == upper) return

        if (!animationsEnabled) {
            offset = clampOffset(offset, scale, viewportSize, contentSize)
            return
        }

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
            centroid = viewportCenter(),
        )
    }

    fun resetImmediate() {
        stopAnimations()
        scale = MIN_ZOOM
        offset = clampOffset(Offset.Zero, MIN_ZOOM, viewportSize, contentSize)
    }

    fun viewportCenter(): Offset =
        Offset(
            x = viewportSize.width / 2f,
            y = viewportSize.height / 2f,
        )
}

@Composable
internal fun rememberZoomPanState(): ZoomPanState = remember { ZoomPanState() }
