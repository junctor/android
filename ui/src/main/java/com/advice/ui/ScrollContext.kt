package com.advice.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

data class DayRange(
    val start: Int,
    val end: Int,
)

data class DaySelectorScrollState(
    val highlight: DayRange,
    val settled: DayRange,
)

/**
 * Maps the schedule list's visible items to day-header indices.
 *
 * - [DaySelectorScrollState.highlight] updates when the visible *day* changes
 *   (not on every list item), including during a fling.
 * - [DaySelectorScrollState.settled] updates only after scrolling stops, so the
 *   day strip can auto-scroll without fighting the list fling.
 */
@Composable
fun rememberDaySelectorScrollState(
    listState: LazyListState,
    headerIndices: List<Int>,
): DaySelectorScrollState {
    val initial = remember(headerIndices) {
        computeDayRange(listState, headerIndices)
    }
    var highlight by remember(headerIndices) { mutableStateOf(initial) }
    var settled by remember(headerIndices) { mutableStateOf(initial) }

    LaunchedEffect(listState, headerIndices) {
        snapshotFlow {
            listState.isScrollInProgress to computeDayRange(listState, headerIndices)
        }
            .distinctUntilChanged()
            .collect { (isScrolling, range) ->
                highlight = range
                if (!isScrolling) {
                    settled = range
                }
            }
    }

    return DaySelectorScrollState(highlight = highlight, settled = settled)
}

private fun computeDayRange(
    listState: LazyListState,
    headerIndices: List<Int>,
): DayRange {
    if (headerIndices.isEmpty()) {
        return DayRange(0, 0)
    }

    val visible = listState.layoutInfo.visibleItemsInfo
    val first = visible.firstOrNull()?.index ?: 0
    val last = visible.lastOrNull()?.index ?: first

    val start = headerIndices.indexOfLast { it <= first }.coerceAtLeast(0)
    val end = headerIndices.indexOfLast { it <= last }.coerceAtLeast(start)

    return DayRange(start = start, end = end)
}
