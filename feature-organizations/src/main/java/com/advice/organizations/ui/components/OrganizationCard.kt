package com.advice.organizations.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme


@Composable
internal fun OrganizationCard(
    title: String,
    media: String?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    Surface(
        color = Color.Transparent,
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
                } else {

                    val colors = listOf(
                        Color(0xFFEABEBE),
                        Color(0xFFBABEEA),
                    )
                    val gradient = Brush.verticalGradient(colors)

                    Box(
                        modifier = modifier
                            .aspectRatio(1.333f)
                            .background(
                                gradient // Gradient background
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Empty or additional content as needed
                    }
                }
            }

            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    title + "\n",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun OrganizationCardPreview() {
    ScheduleTheme {
        OrganizationCard("360 Unicorn Team", "https://i.imgur.com/2xVXZ1B.png")
    }
}