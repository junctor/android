package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Content
import com.advice.core.local.Location
import com.advice.core.local.Session
import com.advice.core.local.Tag
import java.time.Instant

class FakeContentProvider : PreviewParameterProvider<Content> {
    override val values: Sequence<Content>
        get() = content.asSequence()

    companion object {
        val content = listOf(
            Content(
                conference = "THOTCON 0xC",
                title = "DOORS OPEN 喝一杯",
                description = "",
                sessions = listOf(
                    Session(
                        id = 1,
                        timeZone = "America/Chicago",
                        start = Instant.now(),
                        end = Instant.now(),
                        location =
                        Location(
                            -1L,
                            "LOC://AUD - Track 1 / Первый Трек",
                            "Track 1 / Первый Трек",
                            "THOCON 0xC",
                        ),
                    )
                ),
                updated = Instant.now(),
                speakers = emptyList(),
                types =
                listOf(
                    Tag(
                        -1L,
                        "Misc",
                        "",
                        "#e73dd2",
                        -1,
                    ),
                ),
                urls = emptyList(),
            ),
        )
    }
}
