package com.advice.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.unit.dp
import com.advice.ui.preview.PreviewLightDark
import com.advice.ui.theme.HotPink
import com.advice.ui.theme.ScheduleTheme
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.RichTextStyle
import com.halilibo.richtext.ui.material3.Material3RichText
import com.halilibo.richtext.ui.string.RichTextStringStyle

@Composable
fun Paragraph(
    text: String,
    modifier: Modifier = Modifier,
) {
    Material3RichText(
        modifier =
            modifier
                .padding(16.dp),
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
        Markdown(text)
    }
}

@PreviewLightDark
@Composable
private fun ParagraphPreview() {
    ScheduleTheme {
        Paragraph(
            "DEF CON provides a forum for open discussion between participants, where radical viewpoints are welcome and a high degree of skepticism is expected." +
                "- The Dark Tangent[https://www.defcon.org/html/links/dc-policy.html](https://www.defcon.org/html/links/dc-policy.html)",
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
