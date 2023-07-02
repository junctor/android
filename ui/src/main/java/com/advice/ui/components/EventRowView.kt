package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventRowView(
    title: String,
    time: String,
    location: String,
    tags: List<Tag>,
    isBookmarked: Boolean,
    modifier: Modifier = Modifier,
    onBookmark: ((Boolean) -> Unit) = {},
) {
    Row(
        verticalAlignment = Alignment.CenterVertically, modifier = modifier
            .fillMaxWidth()
            //
    ) {
        if (tags.isNotEmpty()) {
            // Category
            Box(
                modifier = Modifier

                    .width(8.dp)
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                    .height(80.dp)
//                    .fillMaxHeight()
//                    .height(IntrinsicSize.Min)
                    .clip(RoundedCornerShape(4.dp))
                    .background(parseColor(tags.first().color))
            )
        }

//        Text("", textAlign = TextAlign.Center, modifier = Modifier.width(85.dp))
        Spacer(modifier = Modifier.width(16.dp))

        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(location, style = MaterialTheme.typography.bodyMedium)
            FlowRow {
                for (tag in tags) {
                    CategoryView(tag)
                }
            }
        }
        BookmarkButton(isBookmarked = isBookmarked) {
            onBookmark(it)
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun EventRowViewPreview() {
    ScheduleTheme {
        Column {
            EventRowView(
                title = "Compelled Decryption",
                time = "5:30\nAM",
                location = "Track 1",
                tags = listOf(
                    createTag(label = "Introduction", color = "#EEAAFF"),
                ),
                isBookmarked = true
            ) {}
            EventRowView(
                title = "Compelled Decryption",
                time = "6:00\nAM",
                location = "Track 1",
                tags = listOf(
                    createTag(label = "Talk", color = "#FF61EEAA"),
                    createTag(label = "Introduction", color = "#EEAAFF"),
                ),
                isBookmarked = false
            ) {}
        }
    }
}
