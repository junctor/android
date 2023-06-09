package com.advice.organizations.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage


@Composable
internal fun OrganizationCard(
    title: String,
    media: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        Column() {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
            ) {
                if (media != null) {
                    AsyncImage(
                        model = media, contentDescription = "logo", modifier = Modifier
                            .background(Color.White)
                            .aspectRatio(1.333f)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    title + "\n",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
            }
        }
    }
}