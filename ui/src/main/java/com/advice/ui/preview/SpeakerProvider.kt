package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Speaker

class SpeakerProvider : PreviewParameterProvider<Speaker> {

    override val values: Sequence<Speaker>
        get() = speakers.asSequence()

    companion object {
        val speakers = listOf(
            Speaker(-1, "John Doe", "", "www.twitter.com", "/john", "CTO")
        )
    }
}