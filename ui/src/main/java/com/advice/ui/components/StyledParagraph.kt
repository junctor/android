package com.advice.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import com.advice.ui.components.markdown.splitMarkdownByHeadings
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.HotPink
import com.advice.ui.theme.ScheduleTheme
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.roundToInt

@Composable
fun Paragraph(
    text: String,
    modifier: Modifier = Modifier,
) {
    MarkdownBody(
        text = text,
        modifier = modifier.padding(16.dp),
        onLinkClicked = safeMarkdownLinkHandler(LocalUriHandler.current),
    )
}

/**
 * Renders markdown split into heading-anchored sections so `#fragment` TOC links can
 * scroll to the matching heading.
 *
 * Must be called as a direct child of the [Column] that owns [scrollState] /
 * `verticalScroll`, so [androidx.compose.ui.layout.LayoutCoordinates.positionInParent]
 * matches the scroll offset.
 */
@Composable
fun ColumnScope.AnchoredMarkdown(
    text: String,
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current
    val scope = rememberCoroutineScope()
    val headingOffsets: SnapshotStateMap<String, Int> =
        remember(text) { mutableStateMapOf() }
    val sections = remember(text) { splitMarkdownByHeadings(text) }

    val onLinkClicked: (String) -> Unit =
        remember(scrollState, headingOffsets, uriHandler, scope) {
            { url ->
                if (url.startsWith("#")) {
                    val slug = url.removePrefix("#")
                    val y = headingOffsets[slug]
                    if (y != null) {
                        scope.launch {
                            scrollState.animateScrollTo(y.coerceIn(0, scrollState.maxValue))
                        }
                    }
                } else {
                    try {
                        uriHandler.openUri(url)
                    } catch (ex: Exception) {
                        Timber.e(ex, "Could not open link: $url")
                    }
                }
            }
        }

    sections.forEachIndexed { index, section ->
        val slug = section.slug
        val sectionModifier =
            modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = if (index == 0) 16.dp else 0.dp,
                    bottom = if (index == sections.lastIndex) 16.dp else 0.dp,
                ).then(
                    if (slug != null) {
                        Modifier.onGloballyPositioned { coordinates ->
                            val y = coordinates.positionInParent().y.roundToInt()
                            if (headingOffsets[slug] != y) {
                                headingOffsets[slug] = y
                            }
                        }
                    } else {
                        Modifier
                    },
                )
        MarkdownBody(
            text = section.content,
            modifier = sectionModifier,
            onLinkClicked = onLinkClicked,
        )
    }
}

@Composable
internal fun MarkdownBody(
    text: String,
    modifier: Modifier = Modifier,
    onLinkClicked: (String) -> Unit,
) {
    RichText(
        modifier = modifier,
        style =
            RichTextStyle(
                stringStyle =
                    RichTextStringStyle(
                        linkStyle =
                            SpanStyle(
                                color = HotPink,
                            ),
                    ),
            ),
    ) {
        Markdown(
            text,
            onLinkClicked = onLinkClicked,
        )
    }
}

private fun safeMarkdownLinkHandler(uriHandler: UriHandler): (String) -> Unit =
    { url ->
        // In-document TOC anchors (#section) are not openable as Android intents.
        if (!url.startsWith("#")) {
            try {
                uriHandler.openUri(url)
            } catch (ex: Exception) {
                // Match MainActivity.openLink: never crash on unopenable URIs.
                Timber.e(ex, "Could not open link: $url")
            }
        }
    }

@PreviewLightDark
@Composable
private fun ParagraphPreview() {
    ScheduleTheme {
        Paragraph(
            "DEF CON provides a forum for open discussion between participants, " +
                "where radical viewpoints are welcome and a high degree of skepticism is expected." +
                "- The Dark Tangent" +
                "[https://www.defcon.org/html/links/dc-policy.html]" +
                "(https://www.defcon.org/html/links/dc-policy.html)",
        )
    }
}

@PreviewLightDark
@Composable
private fun ParagraphWebsitePreview() {
    ScheduleTheme {
        Paragraph(
            "wifireg.defcon.org/android.html<br/><br/>In order to access the DEF CON 30 Wireless Network, you must have already registered.<br/>",
        )
    }
}

@PreviewLightDark
@Composable
private fun AnchoredMarkdownPreview() {
    ScheduleTheme {
        val scrollState = rememberScrollState()
        androidx.compose.foundation.layout.Column {
            AnchoredMarkdown(
                text =
                    """
                    - [Food](#food)

                    ## Food
                    Tacos.
                    """.trimIndent(),
                scrollState = scrollState,
            )
        }
    }
}
