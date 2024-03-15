package com.advice.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.core.utils.TimeUtil
import com.advice.ui.R
import com.advice.ui.components.BookmarkButton
import com.advice.ui.components.CategorySize
import com.advice.ui.components.CategoryView
import com.advice.ui.components.ClickableUrl
import com.advice.ui.components.NoDetailsView
import com.advice.ui.components.Paragraph
import com.advice.ui.components.ProgressSpinner
import com.advice.ui.components.Speaker
import com.advice.ui.preview.FakeEventProvider
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.parseColor
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    event: Event?,
    onBookmark: (Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onTagClicked: (Tag) -> Unit,
    onLocationClicked: (Location) -> Unit,
    onUrlClicked: (String) -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
) {
    val scrollState = rememberScrollState()

    val alpha = remember {
        Animatable(0f)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        event?.title ?: "",
                        modifier = Modifier.alpha(alpha.value),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(painterResource(id = R.drawable.arrow_back), null)
                    }
                },
                actions = {
                    if (event != null) {
                        BookmarkButton(isBookmarked = event.isBookmarked) {
                            onBookmark(it)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = getContainerColour(event).copy(alpha = alpha.value),
                )
            )
        }
    ) { contentPadding ->
        if (event == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(contentPadding),
                contentAlignment = Alignment.Center
            ) {
                ProgressSpinner()
            }
        } else {
            Box(
                Modifier
                    .verticalScroll(scrollState)
            ) {
                EventScreenContent(
                    event,
                    onTagClicked,
                    onLocationClicked,
                    onUrlClicked,
                    onSpeakerClicked,
                    modifier = Modifier.padding(contentPadding)
                )
            }
        }
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }.collect { scrollPosition ->
            Timber.d("Scroll position: $scrollPosition")
            val temp = if (scrollPosition > 0) {
                1f
            } else {
                0f
            }
            alpha.animateTo(temp)
        }
    }
}

fun getContainerColour(event: Event?): Color {
    if (event == null)
        return Color.Transparent
    return parseColor(event.types.first().color)
}

@Composable
private fun HeaderSection(
    title: String,
    categories: List<Tag>,
    date: String,
    time: String,
    location: String,
    onTagClicked: (Tag) -> Unit,
    onLocationClicked: () -> Unit,
) {
    val color = parseColor(categories.first().color)
    Column(
        Modifier
            .drawBehind {
                drawRoundRect(
                    color,
                    // need to add extra height for padding
                    cornerRadius = CornerRadius(16.dp.toPx())
                )
            }
    ) {
        val statusBarHeight = 48.dp
        val toolbarHeight = 48.dp
        Spacer(Modifier.height(statusBarHeight + toolbarHeight))

        Column(Modifier.padding(16.dp)) {

            Text(
                title,
                color = Color.White,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.headlineLarge
            )

            Box(Modifier.padding(vertical = 8.dp)) {
                CategoryView(
                    categories.first(),
                    size = CategorySize.Large,
                    hasIcon = false,
                    modifier = Modifier.clickable { onTagClicked(categories.first()) }
                )
            }

            DetailsCard(
                icon = Icons.Default.DateRange,
                text = date + "\n" + time,
            )

            DetailsCard(
                icon = Icons.Default.LocationOn,
                text = location,
                onClick = onLocationClicked
            )
        }
    }
}

@Composable
private fun DetailsCard(
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val isClickable = onClick != null
    Surface(
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(enabled = isClickable, onClick = onClick ?: {})
                .padding(16.dp)
        ) {
            Icon(icon, null)
            Spacer(Modifier.width(8.dp))
            Text(text, modifier = Modifier.weight(1f))
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EventScreenContent(
    event: Event,
    onTagClicked: (Tag) -> Unit,
    onLocationClicked: (Location) -> Unit,
    onUrlClicked: (String) -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Column(
        Modifier
    ) {
        HeaderSection(
            title = event.title,
            categories = event.types,
            date = TimeUtil.getEventDateStamp(context, event),
            time = TimeUtil.getEventTimeStamp(context, event),
            location = getLocation(event.location),
            onTagClicked = onTagClicked
        ) {
            onLocationClicked(event.location)
        }
        if (event.types.size > 1) {
            FlowRow(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                val tags = event.types.takeLast(event.types.size - 1)
                for (category in tags) {
                    CategoryView(
                        category,
                        size = CategorySize.Medium,
                        modifier = Modifier.clickable {
                            onTagClicked(category)
                        }
                    )
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
                ClickableUrl(
                    label = action.label,
                    url = action.url,
                    onClick = {
                        onUrlClicked(action.url)
                    }, modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        if (event.speakers.isNotEmpty()) {
            Spacer(Modifier.height(16.dp))
            for (speaker in event.speakers) {
                Speaker(
                    speaker = speaker,
                    onSpeakerClicked = { onSpeakerClicked(speaker) },
                )
            }
        }

        if (event.description.isBlank() && event.urls.isEmpty() && event.speakers.isEmpty()) {
            Spacer(Modifier.height(32.dp))
            NoDetailsView()
        }
        Spacer(modifier = Modifier.height(64.dp))
    }
}

private fun getLocation(location: Location): String {
    // Find the dash within name and replace it with a newline.
    val index = location.name.indexOf(" - " + location.shortName)
    if (index != -1) {
        return location.name.substring(0, index) + "\n" + location.shortName
    }
    return location.name
}

@LightDarkPreview
@Composable
private fun EventScreenPreview(
    @PreviewParameter(FakeEventProvider::class) event: Event,
) {
    ScheduleTheme {
        EventScreen(event, {}, {}, {}, {}, {}, {})
    }
}
    