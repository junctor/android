package com.advice.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewParameter
import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.core.local.feedback.FeedbackForm
import com.advice.ui.components.BackButton
import com.advice.ui.components.BookmarkButton
import com.advice.ui.preview.FakeContentProvider
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.parseColor

@Composable
fun ContentScreen(
    content: Content,
    session: Session?,
    relatedContent: List<Content>,
    onBookmark: (Content, Session?, Boolean) -> Unit,
    onBackPressed: () -> Unit,
    onTagClicked: (Tag) -> Unit,
    onLocationClicked: (Location) -> Unit,
    onSessionClicked: (Session) -> Unit,
    onRelatedContentPressed: (Content) -> Unit,
    onUrlClicked: (String) -> Unit,
    onSpeakerClicked: (Speaker) -> Unit,
    onFeedbackClicked: (FeedbackForm) -> Unit,
    onReport: (String) -> Unit,
) {
    val scrollState = rememberScrollState()

    val alpha = remember { Animatable(0f) }

    Scaffold(
        topBar = {
            TopBar(content, session, alpha, onBackPressed, onBookmark)
        },
    ) { contentPadding ->
        Box(
            Modifier
                .verticalScroll(scrollState),
        ) {
            EventScreenContent(
                content = content,
                session = session,
                relatedContent = relatedContent,
                onTagClicked = onTagClicked,
                onLocationClicked = onLocationClicked,
                onSessionClicked = onSessionClicked,
                onRelatedContentPressed = onRelatedContentPressed,
                onBookmark = onBookmark,
                onUrlClicked = onUrlClicked,
                onSpeakerClicked = onSpeakerClicked,
                onFeedbackClicked = onFeedbackClicked,
                onReport = onReport,
                modifier = Modifier.padding(contentPadding),
            )
        }
    }

    LaunchedEffect(scrollState) {
        snapshotFlow { scrollState.value }.collect { scrollPosition ->
            val temp =
                if (scrollPosition > 0) {
                    1f
                } else {
                    0f
                }
            alpha.animateTo(temp)
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBar(
    content: Content,
    session: Session?,
    alpha: Animatable<Float, AnimationVector1D>,
    onBackPressed: () -> Unit,
    onBookmark: (Content, Session?, Boolean) -> Unit,
) {
    val title = content.title
    val isBookmarked = session?.isBookmarked ?: content.isBookmarked

    CenterAlignedTopAppBar(
        title = {
            Text(
                title,
                modifier = Modifier.alpha(alpha.value),
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        },
        navigationIcon = {
            BackButton(
                onClick = onBackPressed,
                tint = Color.White,
            )
        },
        actions = {
            BookmarkButton(
                isBookmarked = isBookmarked,
                onCheckChange = { onBookmark(content, session, it) },
                contentColor = Color.White,
            )
        },
        colors =
            TopAppBarDefaults.topAppBarColors(
                containerColor = getContainerColour(content).copy(alpha = alpha.value),
                scrolledContainerColor = Color.Unspecified,
                navigationIconContentColor = Color.Unspecified,
                titleContentColor = Color.Unspecified,
                actionIconContentColor = Color.Unspecified,
            ),
    )
}

private fun getContainerColour(content: Content): Color {
    if (content.types.isEmpty()) {
        return Color.Black
    }

    return parseColor(content.types.first().color)
}

@PreviewLightDark
@Composable
private fun EventScreenPreview(
    @PreviewParameter(FakeContentProvider::class) content: Content,
) {
    ScheduleTheme {
        ContentScreen(
            content = content.copy(types = emptyList()),
            session = null,
            relatedContent = emptyList(),
            onBookmark = { _, _, _ -> },
            onBackPressed = {},
            onTagClicked = {},
            onLocationClicked = {},
            onSessionClicked = {},
            onRelatedContentPressed = {},
            onUrlClicked = {},
            onSpeakerClicked = {},
            onFeedbackClicked = {},
            onReport = {},
        )
    }
}
