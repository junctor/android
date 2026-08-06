package com.advice.ui.components.pdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import com.advice.ui.components.zoom.ZOOM_BUTTON_STEP
import com.advice.ui.components.zoom.derivedMaxZoom
import com.advice.ui.components.zoom.fitContentSize
import com.advice.ui.components.zoom.rememberZoomPanState
import com.advice.ui.components.zoom.zoomableGestures
import kotlinx.coroutines.launch

/** Zoom above fit where tile LODs are worth loading. */
internal const val TILE_ZOOM_THRESHOLD = 1.05f

@Composable
internal fun ZoomablePdfPage(
    session: PdfRendererSession,
    pageIndex: Int,
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    renderBudget: PdfRenderBudget = PdfRenderBudget.DEFAULT,
    animationsEnabled: Boolean = true,
    showZoomControls: Boolean = false,
    onZoomedChanged: (Boolean) -> Unit = {},
    onHorizontalEdgeChanged: (Boolean) -> Unit = {},
) {
    val zoomState = rememberZoomPanState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(animationsEnabled) {
        zoomState.animationsEnabled = animationsEnabled
    }

    LaunchedEffect(zoomState.isZoomed) {
        onZoomedChanged(zoomState.isZoomed)
    }

    LaunchedEffect(zoomState.atLeftEdge, zoomState.atRightEdge, zoomState.isZoomed) {
        onHorizontalEdgeChanged(
            zoomState.isZoomed && (zoomState.atLeftEdge || zoomState.atRightEdge),
        )
    }

    LaunchedEffect(isActive) {
        if (!isActive) {
            zoomState.resetImmediate()
        }
    }

    val nestedScrollConnection =
        remember(zoomState) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (!zoomState.isZoomed) return Offset.Zero
                    val consumeX =
                        if (zoomState.canPanHorizontally(available.x)) available.x else 0f
                    val consumeY = available.y
                    if (consumeX != 0f || consumeY != 0f) {
                        zoomState.applyTransform(
                            zoomChange = 1f,
                            panChange = Offset(consumeX, consumeY),
                            centroid = zoomState.viewportCenter(),
                            overscroll = false,
                            consumeHorizontal = consumeX != 0f,
                        )
                    }
                    return Offset(consumeX, consumeY)
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (!zoomState.isZoomed) return Velocity.Zero
                    // Absorb fling while zoomed so the pager does not page mid-content.
                    // Edge handoff for paging is via unconsumed drag, not fling.
                    return if (zoomState.canPanHorizontally(available.x)) {
                        available
                    } else {
                        Velocity(0f, available.y)
                    }
                }
            }
        }

    BoxWithConstraints(
        modifier =
            modifier
                .fillMaxSize()
                .clipToBounds()
                .background(Color.White)
                .nestedScroll(nestedScrollConnection)
                .zoomableGestures(zoomState),
    ) {
        val viewport =
            Size(
                width = constraints.maxWidth.toFloat(),
                height = constraints.maxHeight.toFloat(),
            )
        val aspectRatio = session.pageAspectRatio(pageIndex)
        val contentSize = fitContentSize(viewport, aspectRatio)
        val nativeWidth = session.pageWidthPx(pageIndex).toFloat()
        val maxZoom = derivedMaxZoom(nativeWidth, contentSize.width)

        LaunchedEffect(viewport, contentSize, maxZoom) {
            if (viewport != Size.Zero && contentSize != Size.Zero) {
                zoomState.updateLayout(viewport, contentSize, maxZoom)
                zoomState.snapToLayout()
            }
        }

        val scale = zoomState.scale
        val offset = zoomState.offset
        var tilesCoverViewport by remember(pageIndex) { mutableStateOf(false) }
        val hideBase = tilesCoverViewport && scale > TILE_ZOOM_THRESHOLD

        // Base + tiles share one content-space graphicsLayer so pan/zoom never blanks sharpness.
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier =
                    Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                            transformOrigin = TransformOrigin(0f, 0f)
                        },
            ) {
                PdfPageBase(
                    session = session,
                    pageIndex = pageIndex,
                    contentWidthPx = contentSize.width,
                    contentHeightPx = contentSize.height,
                    renderBudget = renderBudget,
                    hidden = hideBase,
                )
                PdfPageTiles(
                    session = session,
                    pageIndex = pageIndex,
                    contentSize = contentSize,
                    viewport = viewport,
                    zoomState = zoomState,
                    renderBudget = renderBudget,
                    onCoverageChanged = { tilesCoverViewport = it },
                )
            }
        }

        if (showZoomControls && isActive) {
            ZoomControls(
                onZoomIn = {
                    scope.launch {
                        zoomState.animateZoomByStep(ZOOM_BUTTON_STEP)
                    }
                },
                onZoomOut = {
                    scope.launch {
                        zoomState.animateZoomByStep(1f / ZOOM_BUTTON_STEP)
                    }
                },
                onFit = {
                    scope.launch { zoomState.reset() }
                },
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
            )
        }
    }
}
