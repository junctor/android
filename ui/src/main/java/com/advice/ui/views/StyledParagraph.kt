package com.advice.ui.views

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.dp
import com.advice.core.HashTagParser
import com.advice.core.utils.HtmlParser
import com.advice.core.utils.Tag
import com.advice.core.utils.UrlParser
import com.advice.ui.preview.LightDarkPreview
import com.advice.ui.theme.*
import com.halilibo.richtext.markdown.Markdown
import com.halilibo.richtext.ui.material3.Material3RichText
import timber.log.Timber


@Composable
fun Paragraph(
    text: String,
    modifier: Modifier = Modifier,
    onLinkClicked: (String) -> Unit,
) {
    val text = text.replace("<br />", "\n")
        .replace("<br/>", "\n")
        .replace("->", "👉")

    val tags = HtmlParser.findHtmlTags(text)
    val string = replaceHtmlTags(text, tags)
    val urls = UrlParser.findAllUrls(string.text)
    val hashTags = HashTagParser.findAllHashTags(string.text)

    val x = buildAnnotatedString {
        append(string)
        for (url in urls) {
            addStyle(
                UrlStyle,
                url.start,
                url.end,
            )
            addStringAnnotation(
                tag = "URL",
                annotation = url.link,
                start = url.start,
                end = url.end,
            )
        }
        for (tag in hashTags) {
            addStyle(
                HashTagStyle,
                tag.start,
                tag.end,
            )
        }
    }

    val onClick = {
    }


    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                //onClick(layoutResult.getOffsetForPosition(pos))
                val it = layoutResult.getOffsetForPosition(pos)
                Timber.e("Paragraph: $it")


                x
                    .getStringAnnotations("URL", it, it)
                    .firstOrNull()?.let { stringAnnotation ->
                        Timber.e("Paragraph: ${stringAnnotation.item}")
                        onLinkClicked(stringAnnotation.item)
                    }
            }
        }
    }

    Material3RichText(
        modifier = modifier
            .padding(16.dp)
    ) {
        Markdown(text)
    }
}

fun replaceHtmlTags(input: String, tags: List<Tag>): AnnotatedString {
    return buildAnnotatedString {
        var currentIndex = 0
        tags.forEach { tag ->
            if (tag.start > currentIndex) {
                append(input.substring(currentIndex, tag.start))
            }
            val style = when (tag) {
                is Tag.EmailTag -> EmailStyle
                is Tag.LinkTag -> UrlStyle
                is Tag.PhoneTag -> PhoneNumberStyle
                is Tag.UnknownTag -> BoldStyle
            }
            pushStyle(style)
            append(tag.content)
            pop()
            currentIndex = tag.end
        }
        if (currentIndex < input.length) {
            append(input.substring(currentIndex))
        }
    }
}

@LightDarkPreview
@Composable
fun ParagraphPreview() {
    ScheduleTheme {
        Paragraph(
            "Write an #regex-expression to match <a href=\"mailto:support@contact.com\">content</a> and <b>content</b> and <font>content</font> while getting the value \"content\", <a href=\"tel:9115552123\">911</a> I am <b>very</b> happy to meet you.\n<br />-> Hello world, welcome to my <a href=\"https://example.com\">website</a>!"
        ) {

        }
    }
}

@LightDarkPreview
@Composable
fun ParagraphWebsitePreview() {
    ScheduleTheme {
        Paragraph(
            "wifireg.defcon.org/android.html<br/><br/>In order to access the DEF CON 30 Wireless Network, you must have already registered.<br/>"
        ) {

        }
    }
}