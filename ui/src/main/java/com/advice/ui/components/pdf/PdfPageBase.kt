package com.advice.ui.components.pdf

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.zoom.baseRenderScale
import com.advice.ui.components.zoom.cappedBitmapSize
import kotlin.math.ceil

@Composable
internal fun PdfPageBase(
    session: PdfRendererSession,
    pageIndex: Int,
    contentWidthPx: Float,
    contentHeightPx: Float,
    renderBudget: PdfRenderBudget,
    hidden: Boolean = false,
) {
    val density = LocalDensity.current
    val contentWidthDp = with(density) { contentWidthPx.toDp() }
    val contentHeightDp = with(density) { contentHeightPx.toDp() }
    val scale =
        baseRenderScale(
            density = density.density,
            fittedWidthPx = contentWidthPx,
            maxBitmapEdge = renderBudget.maxBitmapEdge,
            minScale = renderBudget.minBaseScale,
            maxScale = renderBudget.maxBaseScale,
        )
    val (renderWidth, renderHeight) =
        cappedBitmapSize(
            width = ceil(contentWidthPx * scale).toInt(),
            height = ceil(contentHeightPx * scale).toInt(),
            maxEdge = renderBudget.maxBitmapEdge,
        )

    var bitmap by remember(pageIndex) { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(pageIndex, renderWidth, renderHeight) {
        bitmap = session.renderPage(pageIndex, renderWidth, renderHeight)
    }

    // Recycle only after Compose has dropped the previous ImageBitmap.
    DisposableEffect(bitmap) {
        val toRecycle = bitmap
        onDispose { toRecycle.recycleQuietly() }
    }

    val pageBitmap = bitmap
    if (pageBitmap != null && !pageBitmap.isRecycled) {
        Image(
            bitmap = pageBitmap.asImageBitmap(),
            contentDescription = "PDF page ${pageIndex + 1}",
            contentScale = ContentScale.FillBounds,
            filterQuality = FilterQuality.High,
            modifier =
                Modifier
                    .size(width = contentWidthDp, height = contentHeightDp)
                    .graphicsLayer { alpha = if (hidden) 0f else 1f },
        )
    } else {
        Box(
            modifier = Modifier.size(width = contentWidthDp, height = contentHeightDp),
            contentAlignment = Alignment.Center,
        ) {
            ProgressSpinner()
        }
    }
}
