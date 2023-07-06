package com.advice.schedule.ui.components

import android.os.Parcelable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import kotlinx.parcelize.Parcelize
import timber.log.Timber
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
    val density = LocalDensity.current

    var state by rememberSaveable { mutableStateOf(PanelState()) }

    var size by remember { mutableStateOf(IntSize.Zero) }

    val gutterSize = with(density) { GUTTER_SIZE.dp.toPx() }

    val animatedAlpha by animateFloatAsState(state.alpha)

    val dragState = remember {
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

    Timber.e("currentAnchor: $currentAnchor")

//    val currentAnchor by viewModel.currentAnchor.collectAsState()

    LaunchedEffect(currentAnchor) {
        Timber.e("Triggered!")
        when (currentAnchor) {
            DragAnchors.Start -> dragState.animateTo(DragAnchors.Start)
            DragAnchors.Center -> dragState.animateTo(DragAnchors.Center)
            DragAnchors.End -> dragState.animateTo(DragAnchors.End)
        }
    }

    dragState.updateAnchors(
        DraggableAnchors {
            DragAnchors.Start at size.width - gutterSize
            DragAnchors.Center at 0f
            DragAnchors.End at -size.width + gutterSize
        }
    )


    // A container that has 3 children, the first is the left panel, the second is the main content, and the the third is the right panel. The middle panel can slide to the left or right to reveal the left or right panel.
    Box(
        modifier
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
                            .roundToInt(), 0
                    )
                }
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