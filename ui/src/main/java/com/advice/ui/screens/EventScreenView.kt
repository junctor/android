package com.advice.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.ui.components.ActionView
import com.advice.ui.components.BookmarkButton
import com.advice.ui.components.CategorySize
import com.advice.ui.components.CategoryView
import com.advice.ui.components.NoDetailsView
import com.advice.ui.components.Paragraph
import com.advice.ui.components.SpeakerView
import com.advice.ui.preview.FakeEventProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.parseColor
import com.advice.ui.R
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenView(
    event: Event,
    onBookmark: () -> Unit,
    onBackPressed: () -> Unit,
    onLocationClicked: () -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /*Text(event.title, maxLines = 2, overflow = TextOverflow.Ellipsis)*/ },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.baseline_arrow_back_ios_new_24), null)
                    }
                },
                actions = {
                    BookmarkButton(isBookmarked = event.isBookmarked) {
                        onBookmark()
                    }
                }, colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }) { contentPadding ->
        EventScreenContent(
            event,
            onLocationClicked,
            onSpeakerClicked,
            modifier = Modifier.padding(contentPadding)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EventScreenContent(
    event: Event,
    onLocationClicked: () -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
    modifier: Modifier = Modifier,
) {

    Box(
        Modifier
            .verticalScroll(rememberScrollState())


    ) {
        Column(
            Modifier
            //.padding(16.dp)
        ) {
            HeaderSection(
                event.title,
                event.types,
                getDateTimestamp(event),
                event.location.name,
                onLocationClicked,
                modifier,
            )
            FlowRow(
                Modifier
                    //.background(Color.Black)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                if (event.types.size > 2) {
                    for (tag in event.types.subList(1, event.types.size - 1)) {
                        CategoryView(tag, size = CategorySize.Medium)
                    }
                }
            }

            if (event.description.isNotBlank()) {
                Paragraph(
                    event.description,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
            if (event.urls.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                for (action in event.urls) {
                    ActionView(action.label)
                }
            }
            if (event.speakers.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Speakers", textAlign = TextAlign.Center, modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                )
                for (speaker in event.speakers) {
                    SpeakerView(speaker.name, title = speaker.title) {
                        onSpeakerClicked(speaker)
                    }
                }
            }

            if (event.description.isBlank() && event.urls.isEmpty() && event.speakers.isEmpty()) {
                Spacer(Modifier.height(32.dp))
                NoDetailsView()
            }
        }
    }
}

private fun getDateTimestamp(event: Event): String {
    val x = (event.startTime.time / 1000) / 86400
    val now = Date().time / 1000 / 86400
    val prefix = if (x == now) {
        "Today"
    } else {
        val format = SimpleDateFormat("EEEE, MMMM d")
        format.format(event.startTime)
    }
    val format = SimpleDateFormat("h:mm a")
    return prefix + " - " + format.format(event.startTime) + " to " + format.format(event.end)
}

@Composable
fun HeaderSection(
    title: String,
    categories: List<Tag>,
    date: String,
    location: String,
    onLocationClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val color = parseColor(categories.first().color)
    val color2 = color.copy(alpha = 0.65f)
    Column(
        Modifier
            .drawBehind {
                drawRoundRect(
                    color2,
                    // need to add extra height for padding
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }) {
        val statusBarHeight = 48.dp
        val toolbarHeight = 48.dp
        Spacer(Modifier.height(statusBarHeight + toolbarHeight))

        Column(Modifier.padding(16.dp)) {

            Text(title, style = MaterialTheme.typography.headlineLarge)

            Box(Modifier.padding(vertical = 8.dp)) {
                CategoryView(categories.first(), size = CategorySize.Large, hasIcon = false)
            }

            DetailsCard(Icons.Default.DateRange, null, date.replace(" -", "\n"))

            DetailsCard(
                Icons.Default.LocationOn,
                parseColor(categories.last().color),
                location.replace(" - ", "\n"),
                modifier = Modifier.clickable {
                    onLocationClicked()
                })
        }
    }
}

@Composable
private fun DetailsCard(
    icon: ImageVector,
    tint: Color? = null,
    text: String,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
) {
    var isVisible by remember {
        mutableStateOf(true)
    }

    AnimatedVisibility(isVisible) {
        Surface(
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(0.15f)),
            shape = RoundedCornerShape(8.dp),
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(icon, null, tint = tint ?: LocalContentColor.current)
                Spacer(Modifier.width(8.dp))
                Text(text, modifier = Modifier.weight(1f))
                if (onDismiss != null) {
                    IconButton(onClick = {
                        onDismiss()
                        isVisible = false
                    }) {
                        Icon(Icons.Default.Close, null)
                    }
                }
            }
        }
    }
}

@LightDarkPreview
@Composable
fun EventScreenPreview(
    @PreviewParameter(FakeEventProvider::class) event: Event,
) {
    ScheduleTheme {
        EventScreenView(event, {}, {}, {}, {})
    }
}