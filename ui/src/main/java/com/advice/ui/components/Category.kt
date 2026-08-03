package com.advice.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor
import kotlin.math.min

@Composable
fun Category(
    tag: Tag,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val tagColor = remember(tag.color) { parseColor(tag.color) }

    Row(
        modifier
            .semantics(mergeDescendants = true) {
                role = Role.Checkbox
                selected = tag.isSelected
            }.clickable(onClick = onClick)
            .padding(8.dp),
    ) {
        AnimatedCircleTextView(selected = tag.isSelected, text = tag.label, color = tagColor)
    }
}

@Composable
private fun AnimatedCircleTextView(
    selected: Boolean,
    text: String,
    color: Color,
) {
    val progress by animateFloatAsState(
        targetValue = if (selected) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "CategorySelection",
    )

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp)
                .drawBehind {
                    val radius = min(size.width, size.height) / 2f
                    val unselectedRadius = 4.dp.toPx()
                    if (selected || progress > 0.85f) {
                        drawRoundRect(color = color, cornerRadius = CornerRadius(12.dp.toPx()))
                    } else {
                        val circleRadius = unselectedRadius + (radius - unselectedRadius) * progress
                        drawCircle(
                            color = color,
                            center = Offset(x = circleRadius.coerceAtLeast(unselectedRadius), y = size.height / 2),
                            radius = circleRadius.coerceAtLeast(unselectedRadius),
                        )
                    }
                }.padding(4.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(start = 12.dp, end = 12.dp),
            color = if (selected) Color.White else Color.Unspecified,
        )
    }
}

@PreviewLightDark
@Composable
private fun CategoryPreview() {
    val tag = createTag(label = "Talk", color = "#FF0066")

    ScheduleTheme {
        Surface {
            Column {
                Category(tag, onClick = {})
                Category(tag.copy(isSelected = true), onClick = {})
            }
        }
    }
}
