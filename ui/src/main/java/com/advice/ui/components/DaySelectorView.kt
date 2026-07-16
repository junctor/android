package com.advice.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun DaySelectorView(
    days: List<String>,
    start: Int,
    end: Int,
    stripScrollStart: Int = start,
    stripScrollEnd: Int = end,
    onDaySelected: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    var hasSetup by rememberSaveable {
        mutableStateOf(false)
    }

    var size by remember {
        mutableStateOf(IntSize.Zero)
    }

    val startPosition =
        remember {
            Animatable(-1f)
        }

    val endPosition =
        remember {
            Animatable(-1f)
        }

    val positions: Array<IntSize> =
        remember(days.size) {
            Array(days.size.coerceAtLeast(1)) { IntSize.Zero }
        }

    var positionsVersion by remember(days.size) { mutableIntStateOf(0) }

    val safeStart = start.coerceIn(0, positions.lastIndex)
    val safeEnd = end.coerceIn(safeStart, positions.lastIndex)
    val safeStripStart = stripScrollStart.coerceIn(0, positions.lastIndex)
    val safeStripEnd = stripScrollEnd.coerceIn(safeStripStart, positions.lastIndex)

    val alpha =
        remember {
            Animatable(if (hasSetup) 1f else 0f)
        }
    LaunchedEffect(Unit) {
        alpha.animateTo(1f)
        hasSetup = true
    }

    // Auto-scroll the day strip only from settled day indices so it does not
    // fight an active schedule-list fling.
    LaunchedEffect(safeStripStart, safeStripEnd, size, positionsVersion) {
        if (size == IntSize.Zero) return@LaunchedEffect
        if (positions[safeStripStart] == IntSize.Zero || positions[safeStripEnd] == IntSize.Zero) {
            return@LaunchedEffect
        }

        val stripStartPx = positions.take(safeStripStart).sumOf { it.width }
        val stripEndPx =
            positions.take(safeStripEnd).sumOf { it.width } + positions[safeStripEnd].width
        val targetScrollPosition = stripStartPx + ((stripEndPx - stripStartPx) / 2) - size.width / 2
        scrollState.animateScrollTo(targetScrollPosition.coerceAtLeast(0))
    }

    LaunchedEffect(safeStart, safeEnd, positionsVersion) {
        if (positions[safeStart] != IntSize.Zero) {
            val x = positions.take(safeStart).sumOf { it.width }
            startPosition.set(x.toFloat())
        }
        if (positions[safeEnd] != IntSize.Zero) {
            val x = positions.take(safeEnd).sumOf { it.width }
            endPosition.set(x.toFloat() + positions[safeEnd].width)
        }
    }

    val color = MaterialTheme.colorScheme.primary

    Box(
        Modifier
            .height(IntrinsicSize.Min)
            .alpha(alpha.value)
            .onGloballyPositioned {
                size = it.size
            },
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(scrollState)
                    .drawBehind {
                        val verticalPadding = size.height * 0.25f
                        translate(left = startPosition.value + 8.dp.toPx(), top = verticalPadding) {
                            drawRoundRect(
                                color,
                                size =
                                    Size(
                                        width = (endPosition.value - startPosition.value)
                                            .coerceAtLeast(0f),
                                        height = size.height - (verticalPadding * 2),
                                    ),
                                cornerRadius = CornerRadius(60f),
                            )
                        }
                    }.padding(horizontal = 8.dp, vertical = 16.dp),
        ) {
            days.forEachIndexed { index, day ->
                Box(
                    modifier =
                        Modifier
                            .clickable {
                                onDaySelected(day)
                            }.onGloballyPositioned { coordinates ->
                                val measured = coordinates.size
                                if (positions[index] != measured) {
                                    positions[index] = measured
                                    positionsVersion++
                                }
                            }.padding(10.dp),
                ) {
                    Text(
                        day,
                        modifier = Modifier.padding(horizontal = 12.dp),
                        color = if (index in safeStart..safeEnd) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                }
            }
        }
    }
}

private suspend fun Animatable<Float, AnimationVector1D>.set(value: Float) {
    if (this.value == -1f) {
        snapTo(value)
    } else {
        animateTo(value)
    }
}

@PreviewLightDark
@Composable
private fun DaySelectorViewPreview() {
    ScheduleTheme {
        DaySelectorView(listOf("May 31", "June 1", "June 2"), 0, 1) {
        }
    }
}
