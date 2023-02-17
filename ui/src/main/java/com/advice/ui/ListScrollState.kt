package com.advice.ui

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
data class ScrollContext(
    val start: Int,
    val end: Int,
)

@Composable
fun rememberScrollContext(listState: LazyListState): ScrollContext {
    val scrollContext by remember {
        derivedStateOf {
            val info = listState.layoutInfo.visibleItemsInfo
            ScrollContext(
                start = info.firstOrNull()?.index ?: 0,
                end = info.lastOrNull()?.index ?: 0,
            )
        }
    }
    return scrollContext
}