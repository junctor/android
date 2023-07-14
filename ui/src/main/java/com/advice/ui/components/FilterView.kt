package com.advice.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor

@Composable
internal fun FilterView(tag: Tag, onClick: () -> Unit) {
    val alpha = remember {
        Animatable(0f)
    }

    LaunchedEffect(key1 = tag.isSelected, block = {
        alpha.animateTo(if (tag.isSelected) 1f else 0f)
    })

    val tagColor = parseColor(tag.color)
    Surface(
        border = BorderStroke(1.dp, tagColor.copy(alpha = (if (tag.isSelected) 1.0f else .85f))),
        shape = RoundedCornerShape(12.dp),
        color = tagColor.copy(alpha = (if (tag.isSelected) 1.0f else .0f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable {
                    onClick()
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
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