package com.advice.ui.components.zoom

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.AwaitPointerEventScope
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/** Minimum fling speed (px/s) before a pan decay is started. */
private const val MIN_FLING_VELOCITY = 300f

/** Vertical drag distance (px) that maps to a 2× quick-scale factor. */
private const val QUICK_SCALE_PX = 220f

/**
 * Attaches pan, pinch-zoom, fling, double-tap zoom ladder, and double-tap-drag
 * quick-scale gestures to [state].
 * Must be applied to the untransformed viewport so pointer deltas are in
 * the same coordinate space as [ZoomPanState.offset].
 */
internal fun Modifier.zoomableGestures(state: ZoomPanState): Modifier =
    composed {
        val scope = rememberCoroutineScope()
        val decay = rememberSplineBasedDecay<Offset>()

        Modifier
            .pointerInput(state) {
                detectDoubleTapAndQuickScale(
                    onDoubleTap = { tap ->
                        scope.launch {
                            state.stopAnimations()
                            state.animateDoubleTap(tap)
                        }
                    },
                    onQuickScaleStart = { state.stopAnimations() },
                    onQuickScale = { zoomChange, centroid ->
                        state.applyTransform(
                            zoomChange = zoomChange,
                            panChange = Offset.Zero,
                            centroid = centroid,
                            overscroll = true,
                        )
                    },
                    onQuickScaleEnd = {
                        scope.launch { state.settleAfterGesture() }
                    },
                )
            }.pointerInput(state, decay) {
                detectZoomPanGestures(
                    onGestureStart = { state.stopAnimations() },
                    onGesture = { centroid, pan, zoom ->
                        val consumeHorizontal =
                            !state.isZoomed || state.canPanHorizontally(pan.x)
                        state.applyTransform(
                            zoomChange = zoom,
                            panChange = pan,
                            centroid = centroid,
                            overscroll = true,
                            consumeHorizontal = consumeHorizontal,
                        )
                        // Consume when we used any part of the gesture (incl. vertical /
                        // zoom). Leave unconsumed only for pure outward horizontal edge pans.
                        consumeHorizontal || zoom != 1f || abs(pan.y) > 0.01f
                    },
                    onGestureEnd = { velocity ->
                        scope.launch {
                            when {
                                state.isOverscrolled() -> state.settleAfterGesture()
                                velocity.getDistance() >= MIN_FLING_VELOCITY ->
                                    state.fling(velocity, decay)
                                else -> state.settleAfterGesture()
                            }
                        }
                    },
                )
            }
    }

/**
 * Double-tap zooms via [onDoubleTap]. If the second tap is held and dragged
 * vertically past touch slop, enters quick-scale instead (one-finger zoom).
 */
private suspend fun PointerInputScope.detectDoubleTapAndQuickScale(
    onDoubleTap: (Offset) -> Unit,
    onQuickScaleStart: () -> Unit,
    onQuickScale: (zoomChange: Float, centroid: Offset) -> Unit,
    onQuickScaleEnd: () -> Unit,
) {
    val doubleTapTimeout = viewConfiguration.doubleTapTimeoutMillis
    val doubleTapMinTime = viewConfiguration.doubleTapMinTimeMillis
    val touchSlop = viewConfiguration.touchSlop

    awaitEachGesture {
        awaitFirstDown(requireUnconsumed = false)
        val firstUp = waitForUpOrCancellation() ?: return@awaitEachGesture

        val secondDown =
            awaitSecondDown(
                firstUp = firstUp,
                minTime = doubleTapMinTime,
                timeout = doubleTapTimeout,
            ) ?: return@awaitEachGesture

        val centroid = secondDown.position
        var anchorY = secondDown.position.y
        var lastScaleFactor = 1f
        var quickScaling = false

        while (true) {
            val event =
                if (quickScaling) {
                    awaitPointerEvent(PointerEventPass.Main)
                } else {
                    withTimeoutOrNull(doubleTapTimeout) {
                        awaitPointerEvent(PointerEventPass.Main)
                    } ?: return@awaitEachGesture
                }

            val change =
                event.changes.firstOrNull { it.id == secondDown.id }
                    ?: return@awaitEachGesture
            if (change.changedToUp()) {
                if (quickScaling) {
                    change.consume()
                    onQuickScaleEnd()
                } else {
                    onDoubleTap(centroid)
                }
                return@awaitEachGesture
            }
            if (change.isConsumed) return@awaitEachGesture

            val dy = change.position.y - anchorY
            if (!quickScaling) {
                if (abs(dy) > touchSlop) {
                    quickScaling = true
                    onQuickScaleStart()
                    anchorY = change.position.y
                    lastScaleFactor = 1f
                    change.consume()
                }
            } else {
                val totalDy = anchorY - change.position.y
                val scaleFactor =
                    exp(ln(2.0) * (totalDy / QUICK_SCALE_PX).toDouble()).toFloat()
                val zoomChange =
                    if (lastScaleFactor == 0f) 1f else scaleFactor / lastScaleFactor
                lastScaleFactor = scaleFactor
                if (zoomChange != 1f) {
                    onQuickScale(zoomChange, centroid)
                }
                event.changes.fastForEach {
                    if (it.pressed || it.positionChanged()) it.consume()
                }
            }
        }
    }
}

private suspend fun AwaitPointerEventScope.awaitSecondDown(
    firstUp: PointerInputChange,
    minTime: Long,
    timeout: Long,
): PointerInputChange? =
    withTimeoutOrNull(timeout) {
        var down: PointerInputChange
        do {
            down = awaitFirstDown(requireUnconsumed = false)
        } while (down.uptimeMillis - firstUp.uptimeMillis < minTime)
        down
    }

/**
 * Waits for the currently-down pointer(s) from [awaitFirstDown] to all go up,
 * or returns null if the gesture is cancelled.
 */
private suspend fun AwaitPointerEventScope.waitForUpOrCancellation(): PointerInputChange? {
    while (true) {
        val event = awaitPointerEvent(PointerEventPass.Main)
        if (event.changes.fastAny { it.isConsumed }) return null
        val allUp = event.changes.none { it.pressed }
        if (allUp) {
            return event.changes.firstOrNull { it.changedToUp() }
                ?: event.changes.firstOrNull()
        }
    }
}

/**
 * Pan + pinch detector that reports velocity on release for fling.
 * Mirrors Foundation's detectTransformGestures but adds gesture-end velocity
 * and skips rotation.
 *
 * Fling is suppressed after multi-touch (pinch) gestures — lifting fingers
 * from a pinch produces a large centroid jump that would otherwise fling.
 *
 * [onGesture] returns whether the gesture consumed the pointer event. When false
 * (horizontal edge handoff), pointers are left unconsumed for a parent pager.
 *
 * Callbacks are non-suspending because [awaitEachGesture] runs in a restricted
 * pointer coroutine scope.
 */
private suspend fun PointerInputScope.detectZoomPanGestures(
    onGestureStart: () -> Unit,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float) -> Boolean,
    onGestureEnd: (velocity: Offset) -> Unit,
) {
    awaitEachGesture {
        val velocityTracker = VelocityTracker()
        var zoom = 1f
        var pan = Offset.Zero
        var pastTouchSlop = false
        val touchSlop = viewConfiguration.touchSlop
        var gestureStarted = false
        var maxPointerCount = 0
        var didZoom = false
        var lastPointerCount = 0
        lateinit var event: PointerEvent

        awaitFirstDown(requireUnconsumed = false)
        do {
            event = awaitPointerEvent()
            val canceled = event.changes.fastAny { it.isConsumed }
            if (canceled) break

            val pointerCount = event.changes.count { it.pressed }
            maxPointerCount = maxOf(maxPointerCount, pointerCount)
            // Centroid jumps when a finger lifts; discard that velocity so it
            // cannot become a fling after a pinch.
            if (pointerCount != lastPointerCount) {
                velocityTracker.resetTracking()
                lastPointerCount = pointerCount
            }
            if (pointerCount == 1) {
                trackCentroidVelocity(event, velocityTracker)
            }

            val zoomChange = event.calculateZoom()
            val panChange = event.calculatePan()
            if (zoomChange != 1f) {
                didZoom = true
            }

            if (!pastTouchSlop) {
                zoom *= zoomChange
                pan += panChange
                val centroidSize = event.calculateCentroidSize(useCurrent = false)
                val zoomMotion = abs(1 - zoom) * centroidSize
                val panMotion = pan.getDistance()
                if (zoomMotion > touchSlop || panMotion > touchSlop) {
                    pastTouchSlop = true
                }
            }

            if (pastTouchSlop) {
                if (!gestureStarted) {
                    gestureStarted = true
                    onGestureStart()
                }
                val centroid = event.calculateCentroid(useCurrent = false)
                if (zoomChange != 1f || panChange != Offset.Zero) {
                    val consumed = onGesture(centroid, panChange, zoomChange)
                    if (consumed) {
                        event.changes.fastForEach { change ->
                            if (change.positionChanged()) {
                                change.consume()
                            }
                        }
                    }
                }
            }
        } while (event.changes.fastAny { it.pressed })

        if (gestureStarted) {
            val velocity =
                if (maxPointerCount == 1 && !didZoom) {
                    val v = velocityTracker.calculateVelocity()
                    Offset(v.x, v.y)
                } else {
                    Offset.Zero
                }
            onGestureEnd(velocity)
        }
    }
}

/**
 * Feeds the centroid of currently-down pointers into [tracker] so fling
 * velocity reflects the pan gesture rather than a single finger.
 */
private fun trackCentroidVelocity(
    event: PointerEvent,
    tracker: VelocityTracker,
) {
    var sum = Offset.Zero
    var count = 0
    var latestUptime = 0L
    event.changes.fastForEach { change ->
        if (change.pressed) {
            sum += change.position
            count++
            latestUptime = maxOf(latestUptime, change.uptimeMillis)
        }
    }
    if (count == 0) return
    val centroid = sum / count.toFloat()
    tracker.addPosition(latestUptime, centroid)
}
