package com.advice.ui.components.pdf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import com.advice.ui.components.ProgressSpinner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

@Composable
internal fun PdfDisplay(
    file: File,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Text("PDF: ${file.name}")
        }
        return
    }

    key(file.absolutePath) {
        PdfDisplayContent(file = file, modifier = modifier)
    }
}

@Composable
private fun PdfDisplayContent(
    file: File,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val renderBudget = remember(context) { PdfRenderBudget.from(context) }
    val animationsEnabled = remember(context) { context.areSystemAnimationsEnabled() }

    var session by remember(file.absolutePath) { mutableStateOf<PdfRendererSession?>(null) }
    var loadFailed by remember(file.absolutePath) { mutableStateOf(false) }

    LaunchedEffect(file.absolutePath) {
        session?.close()
        session = null
        loadFailed = false
        var opened: PdfRendererSession? = null
        try {
            opened =
                withContext(Dispatchers.IO) {
                    runCatching { PdfRendererSession(file) }
                        .onFailure { Timber.e(it, "Failed to open PDF: $file") }
                        .getOrNull()
                }
            if (opened == null) {
                loadFailed = true
            } else {
                session = opened
                opened = null
            }
        } finally {
            opened?.close()
        }
    }

    DisposableEffect(file.absolutePath) {
        onDispose {
            session?.close()
            session = null
        }
    }

    val currentSession = session
    if (currentSession == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            if (loadFailed) {
                Text(
                    text = "Unable to open PDF",
                    color = MaterialTheme.colorScheme.onSurface,
                )
            } else {
                ProgressSpinner()
            }
        }
        return
    }

    if (currentSession.pageCount <= 1) {
        ZoomablePdfPage(
            session = currentSession,
            pageIndex = 0,
            renderBudget = renderBudget,
            animationsEnabled = animationsEnabled,
            showZoomControls = true,
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    val pagerState = rememberPagerState { currentSession.pageCount }
    // Track zoom of the current page so the pager doesn't steal mid-content pans.
    // Edge handoff: when zoomed at a horizontal edge, re-enable pager scrolling so
    // unconsumed outward pans can change pages.
    var currentPageZoomed by remember { mutableStateOf(false) }
    var currentPageAtEdge by remember { mutableStateOf(false) }

    HorizontalPager(
        state = pagerState,
        modifier = modifier.fillMaxSize(),
        userScrollEnabled = !currentPageZoomed || currentPageAtEdge,
    ) { pageIndex ->
        val isCurrentPage = pageIndex == pagerState.currentPage
        ZoomablePdfPage(
            session = currentSession,
            pageIndex = pageIndex,
            isActive = isCurrentPage,
            renderBudget = renderBudget,
            animationsEnabled = animationsEnabled,
            showZoomControls = isCurrentPage,
            onZoomedChanged = { zoomed ->
                if (isCurrentPage) {
                    currentPageZoomed = zoomed
                }
            },
            onHorizontalEdgeChanged = { atEdge ->
                if (isCurrentPage) {
                    currentPageAtEdge = atEdge
                }
            },
            modifier = Modifier.fillMaxSize(),
        )
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect {
                currentPageZoomed = false
                currentPageAtEdge = false
            }
    }
}
