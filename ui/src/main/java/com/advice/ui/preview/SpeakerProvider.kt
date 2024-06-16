package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Affiliation
import com.advice.core.local.Link
import com.advice.core.local.Speaker

class SpeakerProvider : PreviewParameterProvider<Speaker> {
    override val values: Sequence<Speaker>
        get() = speakers.asSequence()

    companion object {
        val speakers =
            listOf(
                Speaker(
                    id = -1,
                    name = "John Doe",
                    pronouns = "He/Him",
                    description = "/john",
                    affiliations = listOf(Affiliation("DEF CON", "CTO")),
                    links = listOf(Link("twitter", "www.twitter.com")),
                    roles = listOf(),
                ),
            )
    }
}
