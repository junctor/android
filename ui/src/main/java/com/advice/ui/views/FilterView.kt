package com.advice.ui.views

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.createTag
import java.lang.Float.min

@Composable
fun FilterView(tag: Tag, onClick: () -> Unit) {
    val smallSize = 8f

    val height = remember {
        Animatable(0f)
    }

    val width = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = tag.isSelected, block = {
        height.animateTo(if (tag.isSelected) 24f else smallSize)
    })

    LaunchedEffect(key1 = tag.isSelected, block = {
        width.animateTo(if (tag.isSelected) 1f else 0f)
    })


    Box {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp)
            .drawBehind {
                val width = min(size.width, smallSize.dp.toPx() + width.value * size.width)


                val smallSize = Size(width, height.value.dp.toPx())
                drawRoundRect(
                    color = Color(android.graphics.Color.parseColor(tag.color)),
                    topLeft = Offset(0f, size.height / 2 - height.value.dp.toPx() / 2),
                    size = smallSize,
                    cornerRadius = CornerRadius(40f)
                )
            }) {
            Spacer(Modifier.width(24.dp))
            Text(
                tag.label, color = if (tag.isSelected) MaterialTheme.colorScheme.inverseOnSurface else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterViewPreview() {
    MaterialTheme {
        FilterView(createTag(label = "Talk", color = "#FF0066")) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterViewSelectedPreview() {
    MaterialTheme {
        FilterView(createTag(label = "Talk", color = "#FF0066", isSelected = true)) {

        }
    }
}