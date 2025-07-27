package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor

@Composable
fun CategoryView(
    tag: Tag,
    size: CategorySize = CategorySize.Small,
    color: Color = MaterialTheme.colorScheme.onSurface,
    hasIcon: Boolean = true,
    modifier: Modifier = Modifier,
) {
    val style =
        when (size) {
            CategorySize.Large -> MaterialTheme.typography.bodyLarge
            CategorySize.Medium -> MaterialTheme.typography.bodyMedium
            CategorySize.Small -> MaterialTheme.typography.bodySmall
        }

    val fontWeight =
        when (size) {
            CategorySize.Large -> FontWeight.ExtraBold
            CategorySize.Medium -> MaterialTheme.typography.bodyMedium.fontWeight
            CategorySize.Small -> FontWeight.Medium
        }

    val padding =
        when (size) {
            CategorySize.Large -> 8.dp
            CategorySize.Medium -> 6.dp
            CategorySize.Small -> 4.dp
        }

    val iconSize =
        when (size) {
            CategorySize.Large -> 16.dp
            CategorySize.Medium -> 12.dp
            CategorySize.Small -> 8.dp
        }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier.padding(
                start = 0.dp,
                end = if (hasIcon) padding else 0.dp,
                top = padding / 2,
                bottom = padding / 2,
            ),
        horizontalArrangement = Arrangement.spacedBy(padding),
    ) {
        if (hasIcon) {
            Box(
                Modifier
                    .size(iconSize)
                    .clip(CircleShape)
                    .background(parseColor(tag.color)),
            )
        }
        Text(
            tag.label,
            color = color,
            fontWeight = fontWeight,
            style = style,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewLightDark
@Composable
private fun CategoryViewPreview() {
    val tag = createTag(label = "Talk", color = "#EE11EE")

    ScheduleTheme {
        Surface {
            CategoryView(tag)
        }
    }
}
