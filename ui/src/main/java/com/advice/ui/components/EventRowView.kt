package com.advice.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.utils.TimeUtil
import com.advice.ui.preview.FakeEventProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme

@Composable
fun EventRow(
    event: Event,
    onEventPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val row = remember(event) { event.toScheduleEventUi(context) }
    val dateTime = remember(event.session) {
        TimeUtil.getDateTimeStamp(context, event.session)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clickable {
                    onEventPressed()
                }
                .fillMaxWidth(),
    ) {
        CategoryDash(color = row.dashColor)
        Spacer(Modifier.width(24.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Title(row.title)
            Location(row.location)
            DateTime(dateTime)
            Categories(row.tags)
        }
    }
}

@Composable
fun EventRowView(
    title: String,
    time: String,
    location: String,
    tags: List<EventTagUi>,
    isBookmarked: Boolean,
    onEventPressed: () -> Unit,
    onBookmark: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
    dashColor: Color? = tags.firstOrNull()?.color,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            modifier
                .clickable {
                    onEventPressed()
                }
                .fillMaxWidth(),
    ) {
        CategoryDash(color = dashColor)
        Text(
            time,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(85.dp),
        )
        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Title(title)
            Location(location)
            Categories(tags)
        }
        BookmarkButton(
            isBookmarked = isBookmarked,
            onCheckChange = { onBookmark(it) },
        )
    }
}

@Composable
fun EventRowView(
    row: ScheduleEventUi,
    onEventPressed: () -> Unit,
    onBookmark: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    EventRowView(
        title = row.title,
        time = row.time,
        location = row.location,
        tags = row.tags,
        isBookmarked = row.isBookmarked,
        onEventPressed = onEventPressed,
        onBookmark = onBookmark,
        modifier = modifier,
        dashColor = row.dashColor,
    )
}

@Composable
internal fun CategoryDash(color: Color?, height: Dp = 80.dp) {
    if (color != null) {
        Box(
            modifier =
                Modifier
                    .width(8.dp)
                    .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                    .height(height)
                    .clip(RoundedCornerShape(4.dp))
                    .background(color),
        )
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
internal fun Categories(tags: List<EventTagUi>) {
    val padding = 4.dp
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(padding),
        verticalArrangement = Arrangement.spacedBy(padding),
    ) {
        for (tag in tags) {
            CategoryView(
                label = tag.label,
                indicatorColor = tag.color,
            )
        }
    }
}

@Composable
internal fun Title(title: String) {
    Text(title, fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyMedium)
}

@Composable
internal fun Location(location: String) {
    Text(location, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
}

@Composable
internal fun DateTime(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium)
}

@PreviewLightDark
@Composable
private fun EventRowPreview(
    @PreviewParameter(FakeEventProvider::class) event: Event,
) {
    ScheduleTheme {
        EventRow(
            event = event,
            onEventPressed = {},
        )
    }
}

@PreviewLightDark
@Composable
private fun EventRowViewPreview() {
    ScheduleTheme {
        Column {
            EventRowView(
                title = "Compelled Decryption",
                time = "5:30\nAM",
                location = "Track 1",
                tags =
                    listOf(
                        EventTagUi(
                            label = "Introduction",
                            color = Color(0xFFEEAAFF),
                        ),
                    ),
                isBookmarked = true,
                onEventPressed = {},
                onBookmark = {},
            )
            EventRowView(
                title = "Compelled Decryption",
                time = "6:00\nAM",
                location = "Track 1",
                tags =
                    listOf(
                        EventTagUi(label = "Talk", color = Color(0xFF61EEAA)),
                        EventTagUi(label = "Introduction", color = Color(0xFFEEAAFF)),
                    ),
                isBookmarked = false,
                onEventPressed = {},
                onBookmark = {},
            )
        }
    }
}
