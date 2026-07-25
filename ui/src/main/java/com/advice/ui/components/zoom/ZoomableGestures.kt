package com.advice.ui.components.zoom

import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.input.pointer.util.VelocityTracker
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlinx.coroutines.launch
import kotlin.math.abs

/** Minimum fling speed (px/s) before a pan decay is started. */
private const val MIN_FLING_VELOCITY = 300f

/**
 * Attaches pan, pinch-zoom, fling, and double-tap-zoom gestures to [state].
 * Must be applied to the untransformed viewport so pointer deltas are in
 * the same coordinate space as [ZoomPanState.offset].
 */
internal fun Modifier.zoomableGestures(state: ZoomPanState): Modifier =
    composed {
        val scope = rememberCoroutineScope()
        val decay = rememberSplineBasedDecay<Offset>()

        Modifier
            .pointerInput(state) {
                detectTapGestures(
                    onDoubleTap = { tap ->
                        scope.launch {
                            state.stopAnimations()
                            state.animateDoubleTap(tap)
                        }
                    },
                )
            }.pointerInput(state, decay) {
                detectZoomPanGestures(
                    onGestureStart = { state.stopAnimations() },
                    onGesture = { centroid, pan, zoom ->
                        state.applyTransform(
                            zoomChange = zoom,
                            panChange = pan,
                            centroid = centroid,
                        )
                    },
                    onGestureEnd = { velocity ->
                        scope.launch {
                            state.fling(velocity, decay)
                        }
                    },
                )
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
 * Callbacks are non-suspending because [awaitEachGesture] runs in a restricted
 * pointer coroutine scope.
 */
private suspend fun PointerInputScope.detectZoomPanGestures(
    onGestureStart: () -> Unit,
    onGesture: (centroid: Offset, pan: Offset, zoom: Float) -> Unit,
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
                    onGesture(centroid, panChange, zoomChange)
                }
                event.changes.fastForEach { change ->
                    if (change.positionChanged()) {
                        change.consume()
                    }
                }
            }
        } while (event.changes.fastAny { it.pressed })

        if (gestureStarted && maxPointerCount == 1 && !didZoom) {
            val velocity = velocityTracker.calculateVelocity()
            val offsetVelocity = Offset(velocity.x, velocity.y)
            if (offsetVelocity.getDistance() >= MIN_FLING_VELOCITY) {
                onGestureEnd(offsetVelocity)
            }
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
