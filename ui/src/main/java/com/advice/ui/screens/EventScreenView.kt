package com.advice.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.ui.preview.FakeEventProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.parseColor
import com.advice.ui.views.ActionView
import com.advice.ui.views.BookmarkButton
import com.advice.ui.views.CategoryView
import com.advice.ui.views.NoDetailsView
import com.advice.ui.views.Paragraph
import com.advice.ui.views.SpeakerView
import java.text.SimpleDateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreenView(
    event: Event,
    onBookmark: () -> Unit,
    onBackPressed: () -> Unit,
    onLocationClicked: () -> Unit,
    onSpeakerClicked: (Speaker) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /*Text(event.title, maxLines = 2, overflow = TextOverflow.Ellipsis)*/ },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, null)
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

@Composable
fun EventScreenContent(
    event: Event,
    onLocationClicked: () -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
    modifier: Modifier = Modifier
) {
    val topHeight: MutableState<Float> = remember {
        mutableStateOf(0f)
    }

    val statusBarHeight = with(LocalDensity.current) { 48.dp.toPx() }

    // gradient background
    val brush = Brush.linearGradient(
        colors = listOf(
            MaterialTheme.colorScheme.background,
            parseColor(event.types.first().color),
            parseColor(event.types.first().color)
        ),
        start = Offset(0f, 0f),
        end = Offset(0f, Float.POSITIVE_INFINITY)


    )

    Box(
        Modifier
            .verticalScroll(rememberScrollState())
            .drawBehind {
                drawRoundRect(
                    brush,
                    // need to add extra height for padding
                    size = Size(size.width, topHeight.value + 64.dp.toPx() + 32.dp.toPx()),
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }

    ) {
        Column(
            modifier
                .padding(16.dp)
        ) {
            HeaderSection(
                event.title,
                event.types,
                getDateTimestamp(event),
                event.location.name,
                onLocationClicked,
                modifier = Modifier
                    .onGloballyPositioned {
                        topHeight.value = it.size.height.toFloat() + statusBarHeight
                    }
            )
            if (event.description.isNotBlank()) {
                Paragraph(
                    event.description,
                    modifier = Modifier.padding(vertical = 16.dp)
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
        event.startTime.toString().split(" ").take(3).joinToString(" ")
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
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.headlineLarge)

        Row(Modifier.padding(vertical = 16.dp)) {
            for (tag in categories) {
                CategoryView(tag)
            }
        }
        DetailsCard(Icons.Default.DateRange, date)
//        DetailsCard(Icons.Default.Info, "Tap the location to show all events from this location") {
//            // dismiss
//        }
        DetailsCard(Icons.Default.LocationOn, location, modifier = Modifier.clickable {
            onLocationClicked()
        })
    }
}

@Composable
private fun DetailsCard(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
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
                Icon(icon, null)
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
    @PreviewParameter(FakeEventProvider::class) event: Event
) {
    ScheduleTheme {
        EventScreenView(event, {}, {}, {}, {})
    }
}