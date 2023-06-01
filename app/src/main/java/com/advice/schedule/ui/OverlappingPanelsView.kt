package com.advice.schedule.ui

import android.os.Parcelable
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.math.MathUtils.clamp
import com.advice.ui.theme.ScheduleTheme
import kotlinx.parcelize.Parcelize
import kotlin.math.roundToInt

private const val GUTTER_SIZE = 56
private const val GUTTER_PADDING = 8

@Parcelize
sealed class Panel : Parcelable {
    object Left : Panel()
    object Right : Panel()
    object Main : Panel()
}

@Parcelize
sealed class LockState : Parcelable {
    object Open : LockState()
    object Close : LockState()
    object Unlocked : LockState()
}

@Parcelize
sealed class SwipeDirection : Parcelable {
    object Left : SwipeDirection()
    object Right : SwipeDirection()
    object None : SwipeDirection()
}

@Parcelize
data class PanelState(
    val panel: Panel = Panel.Main,
    val lockState: LockState = LockState.Unlocked,
    val swipeDirection: SwipeDirection = SwipeDirection.None,

    val isDragging: Boolean = false,
    val offsetX: Float = 0f,
    val alpha: Float = 1f,
) : Parcelable

@Composable
fun OverlappingPanelsView(
    leftPanel: @Composable () -> Unit,
    rightPanel: @Composable () -> Unit,
    mainPanel: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onPanelChangedListener: ((Panel) -> Unit)? = null,
) {
    var state by rememberSaveable { mutableStateOf(PanelState()) }

    var size by remember { mutableStateOf(IntSize.Zero) }
    val gutterSize = with(LocalDensity.current) { GUTTER_SIZE.dp.toPx() }

    val animatedOffset by animateFloatAsState(state.offsetX)
    val animatedAlpha by animateFloatAsState(state.alpha)

    var dragStartX by remember { mutableFloatStateOf(0f) }
    var dragStartTime by remember { mutableLongStateOf(0L) }
    var dragEndX by remember { mutableFloatStateOf(0f) }


    // A container that has 3 children, the first is the left panel, the second is the main content, and the the third is the right panel. The middle panel can slide to the left or right to reveal the left or right panel.
    Box(
        modifier
            .fillMaxSize()
            .onSizeChanged { newSize ->
                size = newSize
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { start ->
                        state = state.copy(isDragging = true)

                        dragStartX = start.x
                        dragEndX = start.x
                        dragStartTime = System.currentTimeMillis()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()

                        dragEndX += dragAmount

                        state = state.copy(
                            offsetX = clamp(
                                state.offsetX + dragAmount,
                                -size.width + gutterSize,
                                size.width - gutterSize,
                            ),
                            alpha = 1f,
                        )
                    },
                    onDragEnd = {
                        state = state.copy(isDragging = false)

                        val dragEndTime = System.currentTimeMillis()
                        val dragDuration = dragEndTime - dragStartTime
                        val dragDistance = dragEndX - dragStartX
                        val velocity = dragDistance / dragDuration * 1000

                        val swipeDirection = when {
                            velocity > 1000f -> SwipeDirection.Right
                            velocity < -1000f -> SwipeDirection.Left
                            else -> SwipeDirection.None
                        }

                        if (swipeDirection == SwipeDirection.None) {
                            when {
                                state.offsetX > size.width / 2 -> {
                                    state = state.copy(
                                        panel = Panel.Left,
                                        offsetX = size.width - gutterSize
                                    )
                                    onPanelChangedListener?.invoke(state.panel)
                                }

                                state.offsetX < -size.width / 2 -> {
                                    state = state.copy(
                                        panel = Panel.Right,
                                        offsetX = -size.width + gutterSize
                                    )
                                    onPanelChangedListener?.invoke(state.panel)
                                }

                                else -> {
                                    state = state.copy(panel = Panel.Main, offsetX = 0f)
                                    onPanelChangedListener?.invoke(state.panel)
                                }
                            }
                            return@detectHorizontalDragGestures
                        }

                        when (state.panel) {
                            Panel.Left -> when (swipeDirection) {
                                SwipeDirection.Left -> {
                                    state = state.copy(panel = Panel.Main, offsetX = 0f)
                                    onPanelChangedListener?.invoke(state.panel)
                                }

                                SwipeDirection.Right -> {
                                    // no-op
                                }

                                SwipeDirection.None -> {
                                    // no-op
                                }
                            }

                            Panel.Main -> {
                                when (swipeDirection) {
                                    SwipeDirection.Left -> {
                                        state = state.copy(
                                            panel = Panel.Right,
                                            offsetX = -size.width + gutterSize
                                        )

                                        onPanelChangedListener?.invoke(state.panel)
                                    }

                                    SwipeDirection.Right -> {
                                        state = state.copy(
                                            panel = Panel.Left,
                                            offsetX = size.width - gutterSize
                                        )
                                        onPanelChangedListener?.invoke(state.panel)
                                    }

                                    SwipeDirection.None -> {
                                        // no-op
                                    }
                                }
                            }

                            Panel.Right -> when (swipeDirection) {
                                SwipeDirection.Right -> {
                                    state = state.copy(panel = Panel.Main, offsetX = 0f)
                                    onPanelChangedListener?.invoke(state.panel)
                                }

                                SwipeDirection.Left -> {
                                    // no-op
                                }

                                SwipeDirection.None -> {
                                    // no-op
                                }
                            }
                        }
                        state =
                            state.copy(alpha = if (state.isDragging || state.panel == Panel.Main) 1.0f else 0.65f)
                    },
                )
            }
            .pointerInput(Unit) {
                detectTapGestures(onTap = { offset ->
                    //tryAwaitRelease()
                    when (state.panel) {
                        Panel.Left -> {
                            if (offset.x > size.width - gutterSize) {
                                state = state.copy(panel = Panel.Main, offsetX = 0f)
                                state =
                                    state.copy(alpha = if (state.isDragging || state.panel == Panel.Main) 1.0f else 0.65f)
                                onPanelChangedListener?.invoke(state.panel)
                            }
                        }

                        Panel.Main -> {
                            // no-op
                        }

                        Panel.Right -> {
                            if (offset.x < gutterSize) {
                                state = state.copy(panel = Panel.Main, offsetX = 0f)
                                state =
                                    state.copy(alpha = if (state.isDragging || state.panel == Panel.Main) 1.0f else 0.65f)
                                onPanelChangedListener?.invoke(state.panel)
                            }
                        }
                    }
                })
            }
    ) {
        // The left panel
        if (animatedOffset > 0) {
            Box(
                Modifier
                    .systemBarsPadding()
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
                    .padding(start = (GUTTER_SIZE + GUTTER_PADDING).dp, end = GUTTER_PADDING.dp)
            ) {
                rightPanel()
            }
        }

        // The main content should be draggable horizontally to reveal the left and right panels.
        Box(
            Modifier
                .offset { IntOffset(animatedOffset.roundToInt(), 0) }
                .alpha(animatedAlpha)
        ) {
            mainPanel()
        }
    }
}

private fun setAlphaState(
    isDragging: Boolean,
    selectedPanel: Panel,
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