package com.advice.ui.views

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
        Box(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 16.dp)
                .width(with(LocalDensity.current) { (endPosition.value - startPosition.value).toDp() })
                .fillMaxHeight()
                .offset(with(LocalDensity.current) { startPosition.value.toDp() }, 0.dp)
//                .clip(CircleShape)
                //.background(Color.Blue)
                .border(3.dp, Color.Black)
                .animateContentSize()
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
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