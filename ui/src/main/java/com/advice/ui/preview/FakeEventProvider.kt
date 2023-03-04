package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Event
import com.advice.core.local.Location
import java.util.Date

class FakeEventProvider : PreviewParameterProvider<Event> {


    override val values: Sequence<Event>
        get() = events.asSequence()


    val events = listOf(
        Event(
            0,
            "DEFCON",
            "Keynote",
            "Hello World",
            Date(),
            Date(),
            "",
            listOf(SpeakerProvider.speakers.random()),
            listOf(TagProvider.tags.random()),
            Location(-1, "Main Stage", "Stage", null, "DEFCON"),
            listOf(),
            false
        )

    )
}