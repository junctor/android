package com.advice.ui.views

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class BubblePosition(val start: Int, val width: Int) {
    companion object {
        val Zero = BubblePosition(0, 0)
    }
}

@Composable
fun DaySelectorView(days: List<String>) {
    var startPosition by remember {
        mutableStateOf(BubblePosition.Zero)
    }


    Box(Modifier.height(IntrinsicSize.Min)) {
        // todo:
//        Box(
//            modifier = Modifier
//                .width(startPosition.width.dp)
//                .padding(vertical = 4.dp)
//                .offset(startPosition.start.dp, 0.dp)
//                .clip(CircleShape)
//                .fillMaxHeight()
//                .background(Color.Red)
//        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(8.dp)
        ) {
            days.forEach {
                Text(it, style = MaterialTheme.typography.titleLarge, modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .onGloballyPositioned {
                        if (startPosition == BubblePosition.Zero) {
                            val x = it.positionInParent().x.toInt()
                            startPosition = BubblePosition(x, it.size.width)
                        }
                    })
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun DaySelectorViewPreview() {
    MaterialTheme {
        DaySelectorView(listOf("May 31", "June 1", "June 2"))
    }
}