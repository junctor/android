package com.advice.schedule.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import kotlin.math.roundToInt

private const val GUTTER_SIZE = 56
private const val GUTTER_PADDING = 8

enum class DragAnchors {
    Start,
    Center,
    End,
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OverlappingPanelsView(
    currentAnchor: DragAnchors,
    leftPanel: @Composable () -> Unit,
    rightPanel: @Composable () -> Unit,
    mainPanel: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onPanelChangedListener: ((DragAnchors) -> Unit)? = null,
) {
    val isComposableReady = remember { mutableStateOf(false) }

    val density = LocalDensity.current

    var size by remember { mutableStateOf(IntSize.Zero) }

    val gutterSize = with(density) { GUTTER_SIZE.dp.toPx() }

    val dragState =
        rememberSaveable(
            saver = object : Saver<AnchoredDraggableState<DragAnchors>, Any> {
                override fun restore(value: Any): AnchoredDraggableState<DragAnchors> {
                    // Your logic for restoring the state from the saved value
                    val restoredAnchor = when (value as String) {
                        "start" -> DragAnchors.Start
                        "center" -> DragAnchors.Center
                        "end" -> DragAnchors.End
                        else -> DragAnchors.Center // Default value
                    }

                    return AnchoredDraggableState(
                        initialValue = restoredAnchor,
                        positionalThreshold = { distance: Float -> distance * 0.5f },
                        velocityThreshold = { with(density) { 100.dp.toPx() } },
                        animationSpec = tween(),
                        confirmValueChange = { anchor ->
                            onPanelChangedListener?.invoke(anchor)
                            true
                        }
                    )
                }

                override fun SaverScope.save(value: AnchoredDraggableState<DragAnchors>): Any {
                    // Your logic for saving the state to a persistent value
                    return when (value.currentValue) {
                        DragAnchors.Start -> "start"
                        DragAnchors.Center -> "center"
                        DragAnchors.End -> "end"
                        else -> "center" // Default value
                    }
                }
            }
        ) {
            AnchoredDraggableState(
                initialValue = DragAnchors.Start,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { with(density) { 100.dp.toPx() } },
                animationSpec = tween(),
                confirmValueChange = { anchor ->
                    onPanelChangedListener?.invoke(anchor)
                    true
                }
            )
        }

    dragState.updateAnchors(
        DraggableAnchors {
            DragAnchors.Start at size.width - gutterSize
            DragAnchors.Center at 0f
            DragAnchors.End at -size.width + gutterSize
        }
    )

    LaunchedEffect(currentAnchor, isComposableReady.value) {
        if (isComposableReady.value) {
            when (currentAnchor) {
                DragAnchors.Start -> dragState.animateTo(DragAnchors.Start)
                DragAnchors.Center -> dragState.animateTo(DragAnchors.Center)
                DragAnchors.End -> dragState.animateTo(DragAnchors.End)
            }
        }
    }

    // A container that has 3 children, the first is the left panel, the second is the main content, and the the third is the right panel. The middle panel can slide to the left or right to reveal the left or right panel.
    Box(
        modifier
            .onGloballyPositioned { isComposableReady.value = true }
            .fillMaxSize()
            .anchoredDraggable(
                dragState, Orientation.Horizontal,
            )
            .onSizeChanged { newSize ->
                size = newSize
            }
    ) {
        // The left panel
        if (dragState.requireOffset() > 0) {
            Box(
                Modifier
                    .systemBarsPadding()
                    .padding(start = GUTTER_PADDING.dp, end = (GUTTER_SIZE + GUTTER_PADDING).dp)
            ) {
                leftPanel()
            }
        }

        // The right panel
        if (dragState.requireOffset() < 0) {
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
                .offset {
                    IntOffset(
                        dragState
                            .requireOffset()
                            .roundToInt(),
                        0
                    )
                }
                .alpha(1.0f)
        ) {
            mainPanel()
        }

        if (currentAnchor != DragAnchors.Center) {
            val alignment = when (currentAnchor) {
                DragAnchors.Start -> Alignment.CenterEnd
                DragAnchors.Center -> Alignment.Center
                DragAnchors.End -> Alignment.CenterStart
            }
            Box(
                modifier = Modifier
                    .width(GUTTER_SIZE.dp)
                    .fillMaxHeight()
                    .align(alignment)
                    .clickable {
                        onPanelChangedListener?.invoke(DragAnchors.Center)
                    }
            )
        }
    }
}

@Preview
@Composable
fun OverlappingPanelsViewStartPreview() {
    ScheduleTheme {
        OverlappingPanelsView(
            currentAnchor = DragAnchors.Start,
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

@Preview
@Composable
fun OverlappingPanelsViewCenterPreview() {
    ScheduleTheme {
        OverlappingPanelsView(
            currentAnchor = DragAnchors.Center,
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

@Preview
@Composable
fun OverlappingPanelsViewEndPreview() {
    ScheduleTheme {
        OverlappingPanelsView(
            currentAnchor = DragAnchors.End,
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
