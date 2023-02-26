package com.advice.ui.views

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.advice.ui.theme.ScheduleTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DaySelectorView(days: List<String>, start: Int, end: Int, onDaySelected: (String) -> Unit) {
    val coroutineScope = rememberCoroutineScope()

    val startPosition = remember {
        Animatable(0f)
    }

    val endPosition = remember {
        Animatable(0f)
    }

    val positions: Array<IntSize> = remember {
        Array(days.size) { IntSize.Zero }
    }

    if (positions[start] != IntSize.Zero) {
        val x = positions.take(start).sumOf { it.width }

        startPosition.set(x.toFloat(), coroutineScope)

    }
    if (positions[end] != IntSize.Zero) {
        val x = positions.take(end).sumOf { it.width }

        endPosition.set(x.toFloat() + positions[end].width, coroutineScope)
    }

    val color = MaterialTheme.colorScheme.primary

    Box(
        Modifier
            .height(IntrinsicSize.Min)

    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .drawBehind {
                    val verticalPadding = size.height * 0.25f
                    translate(left = startPosition.value + 8.dp.toPx(), top = verticalPadding) {
                        drawRoundRect(
                            color, size = Size(
                                width = endPosition.value - startPosition.value,
                                height = size.height - (verticalPadding * 2)
                            ),
                            cornerRadius = CornerRadius(60f)
                        )
                    }
                }
                .padding(horizontal = 8.dp, vertical = 16.dp)
        ) {
            days.forEachIndexed { index, it ->
                Box(modifier = Modifier
                    .clickable {
                        onDaySelected(it)
                    }
                    .onGloballyPositioned {
                        if (positions[index] == IntSize.Zero) {
                            positions[index] = it.size
                        }
                    }
                    .padding(10.dp)
                ) {
                    Text(it, modifier = Modifier.padding(horizontal = 12.dp), color = if (index in start..end) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

private fun Animatable<Float, AnimationVector1D>.set(value: Float, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        animateTo(value)
    }
}

@Preview(showBackground = true)
@Composable
fun DaySelectorViewPreview() {
    ScheduleTheme {
        DaySelectorView(listOf("May 31", "June 1", "June 2"), 0, 1) {

        }
    }
}