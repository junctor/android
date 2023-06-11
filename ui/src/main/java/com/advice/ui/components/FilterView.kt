package com.advice.ui.components

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor

@Composable
internal fun FilterView(tag: Tag, onClick: () -> Unit) {
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
        val color = MaterialTheme.colorScheme.background
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(16.dp)
            .drawBehind {
                val colorWidth = 8.dp.toPx() + width.value * 4.dp.toPx()
                val smallSize = Size(colorWidth, colorWidth)
                val dotWidth = 6.dp.toPx() - width.value * 6.dp.toPx()
                val dotSize = Size(dotWidth, dotWidth)


                drawRoundRect(
                    color = parseColor(tag.color),
                    topLeft = Offset(0f, size.height / 2 - colorWidth / 2),
                    size = smallSize,
                    cornerRadius = CornerRadius(40f)
                )

                drawRoundRect(
                    color = color,
                    topLeft = Offset(1.dp.toPx(), size.height / 2 - dotSize.height / 2),
                    size = dotSize,
                    cornerRadius = CornerRadius(40f)
                )


            }) {
            Spacer(Modifier.width(24.dp))
            Text(
                tag.label,
                Modifier.alpha(if (tag.isSelected) 1.0f else .75f)
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun FilterViewPreview() {
    ScheduleTheme {
        FilterView(createTag(label = "Talk", color = "#FF0066")) {

        }
    }
}

@LightDarkPreview
@Composable
private fun FilterViewSelectedPreview() {
    ScheduleTheme {
        FilterView(createTag(label = "Talk", color = "#FF0066", isSelected = true)) {

        }
    }
}