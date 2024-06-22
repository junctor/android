package com.advice.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.advice.core.local.Tag
import com.advice.ui.preview.PreviewLightDark

@Composable
fun ContentRow(
    title: String,
    tags: List<Tag>,
    isBookmarked: Boolean,
    onContentPressed: () -> Unit,
    onBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clickable {
                    onContentPressed()
                }.fillMaxWidth(),
    ) {
        CategoryDash(tags, height = 52.dp)
        Spacer(modifier = Modifier.width(24.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Title(title)
            Categories(tags)
        }
        BookmarkButton(isBookmarked = isBookmarked) {
            onBookmark(it)
        }
    }
}

@PreviewLightDark
@Composable
private fun ContentRowPreview() {
}
