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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Tag
import com.advice.core.utils.TimeUtil
import com.advice.ui.preview.FakeEventProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.createTag
import com.advice.ui.utils.parseColor

@Composable
fun EventRow(
    event: Event,
    onEventPressed: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                onEventPressed()
            }
            .fillMaxWidth()
    ) {
        CategoryDash(event.types)
        Spacer(Modifier.width(24.dp))
        Column(
            Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Title(event.title)
            Location(event.location.name)
            DateTime(TimeUtil.getDateTimeStamp(context, event))
            Categories(event.types)
        }
    }
}

@Composable
fun EventRowView(
    title: String,
    time: String,
    location: String,
    tags: List<Tag>,
    isBookmarked: Boolean,
    onEventPressed: () -> Unit,
    onBookmark: ((Boolean) -> Unit),
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                onEventPressed()
            }
            .fillMaxWidth()
    ) {
        CategoryDash(tags)
        Time(time)
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
        BookmarkButton(isBookmarked = isBookmarked) {
            onBookmark(it)
        }
    }
}

@Composable
private fun CategoryDash(tags: List<Tag>) {
    if (tags.isNotEmpty()) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .padding(start = 4.dp, top = 4.dp, bottom = 4.dp)
                .height(80.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(parseColor(tags.first().color))
        )
    }
}

@Composable
private fun Time(time: String) {
    val parts = time.split(" - ")
    val begin = parts[0]
    val end = parts.getOrNull(1)

    Column(
        Modifier.width(85.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            begin,
            textAlign = TextAlign.Center
        )
        if (end != null) {
            Box(
                Modifier
                    .padding(vertical = 3.dp)
                    .height(1.dp)
                    .width(6.dp)
                    .background(MaterialTheme.colorScheme.onSurface)
            )
            Text(
                end,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun Categories(tags: List<Tag>) {
    val padding = 4.dp
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(padding),
        verticalArrangement = Arrangement.spacedBy(padding)
    ) {
        for (tag in tags) {
            CategoryView(tag)
        }
    }
}

@Composable
private fun Title(title: String) {
    Text(title, fontWeight = FontWeight.Black, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun Location(location: String) {
    Text(location, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
}

@Composable
private fun DateTime(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium)
}

@LightDarkPreview
@Composable
private fun EventRowPreview(@PreviewParameter(FakeEventProvider::class) event: Event) {
    ScheduleTheme {
        EventRow(
            event = event,
            onEventPressed = {},
        )
    }
}

@LightDarkPreview
@Composable
private fun EventRowViewPreview() {
    ScheduleTheme {
        Column {
            EventRowView(
                title = "Compelled Decryption",
                time = "5:30 AM - 7:00 AM",
                location = "Track 1",
                tags = listOf(
                    createTag(label = "Introduction", color = "#EEAAFF"),
                ),
                isBookmarked = true,
                onEventPressed = {},
                onBookmark = {},
            )
            EventRowView(
                title = "Compelled Decryption",
                time = "6:00 AM - 8:00 AM",
                location = "Track 1",
                tags = listOf(
                    createTag(label = "Talk", color = "#FF61EEAA"),
                    createTag(label = "Introduction", color = "#EEAAFF"),
                ),
                isBookmarked = false,
                onEventPressed = {},
                onBookmark = {},
            )
            EventRowView(
                title = "Compelled Decryption",
                time = "7:00 AM",
                location = "Track 1",
                tags = listOf(
                    createTag(label = "Introduction", color = "#EEAAFF"),
                ),
                isBookmarked = false,
                onEventPressed = {},
                onBookmark = {},
            )
        }
    }
}
