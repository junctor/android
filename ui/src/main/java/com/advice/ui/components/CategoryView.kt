package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor

@Composable
internal fun CategoryView(tag: Tag) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
        Box(
            Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(parseColor(tag.color))
        )
        Spacer(Modifier.width(4.dp))
        Text(tag.label, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Preview(showBackground = true)
@Composable
private fun CategoryViewPreview() {
    ScheduleTheme {
        CategoryView(createTag(label = "Talk", color = "#EE11EE"))
    }
}