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
import com.advice.ui.Colors
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
                    translate(left = startPosition.value, top = verticalPadding) {
                        drawRoundRect(
                            Colors.controlActive, size = Size(
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
                    .padding(10.dp)
                    .onGloballyPositioned {
                        val x = it.positionInParent().x

                        if (index == start) {
                            startPosition.set(x, coroutineScope)
                        }

                        if (index == end) {
                            endPosition.set(it.positionInParent().x + it.size.width, coroutineScope)
                        }
                    }) {
                    Text(it, modifier = Modifier.padding(horizontal = 12.dp))
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
    MaterialTheme {
        DaySelectorView(listOf("May 31", "June 1", "June 2"), 0, 1) {

        }
    }
}