package com.advice.schedule.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import timber.log.Timber
import kotlin.math.roundToInt

private const val GUTTER_SIZE = 56
private const val GUTTER_PADDING = 8


sealed class Panel {
    object Left : Panel()
    object Right : Panel()
    object Main : Panel()
}

sealed class LockState {
    object Open : LockState()
    object Close : LockState()
    object Unlocked : LockState()
}

sealed class SwipeDirection {
    object Left : SwipeDirection()
    object Right : SwipeDirection()
}

@Composable
fun OverlappingPanelsView(
    leftPanel: @Composable () -> Unit,
    rightPanel: @Composable () -> Unit,
    mainPanel: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onPanelChangedListener: ((Panel) -> Unit)? = null,
) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    var selectedPanel by remember { mutableStateOf<Panel>(Panel.Main) }

    var isDragging by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(offsetX)

    var alpha by remember { mutableStateOf(1f) }
    val animatedAlpha by animateFloatAsState(alpha)

    val gutterSize = with(LocalDensity.current) { GUTTER_SIZE.dp.toPx() }



    // A container that has 3 children, the first is the left panel, the second is the main content, and the the third is the right panel. The middle panel can slide to the left or right to reveal the left or right panel.
    Box(
        modifier
//            .background(Color.Yellow)
            .fillMaxSize()
            .onSizeChanged { newSize ->
                size = newSize
            }
//            .pointerInput(Unit) {
//                forEachGesture {
//                    awaitPointerEventScope {
//                        val velocityTracker = VelocityTracker()
//                        val down = awaitFirstDown()
//                        var change = down
//                        var overSlop = false
//                        while (change.pressed) {
//                            if (!overSlop && change.positionChange().getManhattanDistance() >
//                                viewConfiguration.touchSlop) {
//                                overSlop = true
//                            }
//                            if (overSlop) {
//                                velocityTracker.addPosition(
//                                    change.uptimeMillis,
//                                    change.position
//                                )
//                                offsetX += change.positionChange().x
//                            }
//                            change = awaitPointerEvent()
//                        }
//                        val velocity = velocityTracker.calculateVelocity().x
//                        if (abs(velocity) > 1000f) {
//                            // It's a fling!
//                            // Use the velocity to animate the box...
//                        }
//                    }
//                }
//            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        isDragging = false
                        when {
                            offsetX > size.width / 2 -> {
                                selectedPanel = Panel.Left
                                offsetX = size.width - gutterSize
                                onPanelChangedListener?.invoke(selectedPanel)
                            }

                            offsetX < -size.width / 2 -> {
                                selectedPanel = Panel.Right
                                offsetX = -size.width + gutterSize
                                onPanelChangedListener?.invoke(selectedPanel)
                            }

                            else -> {
                                selectedPanel = Panel.Main
                                offsetX = 0f
                                onPanelChangedListener?.invoke(selectedPanel)
                            }
                        }
                        alpha = setAlphaState(isDragging, selectedPanel)
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        Timber.e("onDrag: $dragAmount")
                        isDragging = true
                        alpha = setAlphaState(isDragging, selectedPanel)
                        change.consumeAllChanges()
                        offsetX += dragAmount
                    })
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    Timber.e("onPress: $selectedPanel, $offset")
                    //tryAwaitRelease()
                    when (selectedPanel) {
                        Panel.Left -> {
                            if (offset.x > size.width - gutterSize) {
                                selectedPanel = Panel.Main
                                offsetX = 0f
                                alpha = setAlphaState(isDragging, selectedPanel)
                                onPanelChangedListener?.invoke(selectedPanel)
                            }
                        }

                        Panel.Main -> {
                            // no-op
                        }

                        Panel.Right -> {
                            if (offset.x < gutterSize) {
                                selectedPanel = Panel.Main
                                offsetX = 0f
                                alpha = setAlphaState(isDragging, selectedPanel)
                                onPanelChangedListener?.invoke(selectedPanel)
                            }
                        }
                    }
                })
            }) {
        // The left panel
        if (animatedOffset > 0) {
            Box(
                Modifier
                    .systemBarsPadding()
//                    .background(Color.Green)
                    .padding(start = GUTTER_PADDING.dp, end = (GUTTER_SIZE + GUTTER_PADDING).dp)
            ) {
                leftPanel()
            }
        }

        // The right panel
        if (animatedOffset < 0) {
            Box(
                Modifier
                    .systemBarsPadding()
//                    .background(Color.Red)
                    .padding(start = (GUTTER_SIZE + GUTTER_PADDING).dp, end = GUTTER_PADDING.dp)
            ) {
                rightPanel()
            }

        }

        // The main content should be draggable horizontally to reveal the left and right panels.
        Box(
            Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
//                .pointerInput(Unit) {
//                    this.detectDragGestures { change, dragAmount ->
//                        Timber.e("onDrag: $selectedPanel")
//                        if(selectedPanel != Panel.Main) {
//                            change.consume()
//                        }
//                    }
//                }
                .alpha(animatedAlpha)
        ) {
            mainPanel()
        }
    }
}

private fun setAlphaState(
    isDragging: Boolean,
    selectedPanel: Panel
): Float {
    return if (isDragging || selectedPanel == Panel.Main) 1.0f else 0.65f
}

@Preview
@Composable
fun OverlappingPanelsViewPreview() {
    ScheduleTheme {
        OverlappingPanelsView(
            leftPanel = {
                Box(
                    Modifier
                        .background(Color.Blue)
                        .fillMaxSize()
                )
            },
            rightPanel = {
                Box(
                    Modifier
                        .background(Color.Red)
                        .fillMaxSize()
                )
            },
            mainPanel = {
                Box(
                    Modifier
                        .background(Color.Green)
                        .fillMaxSize()
                )
            }
        )
    }
}