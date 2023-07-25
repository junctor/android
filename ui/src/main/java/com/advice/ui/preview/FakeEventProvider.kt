package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Event
import com.advice.core.local.Location
import java.time.Instant
import java.util.Date

class FakeEventProvider : PreviewParameterProvider<Event> {


    override val values: Sequence<Event>
        get() = events.asSequence()


    val events = listOf(
        Event(
            0,
            "DEFCON",
            "America/Los_Angeles",
            "Payment Hacking Challenge",
            "Try yourself in ATM, Online bank, POST and Cards hacking challenges.\nPlease join the DEF CON Discord and see the #payv-labs-text channel for more information.",
            Instant.now(),
            Instant.now(),
            Instant.now(),
            SpeakerProvider.speakers.take(2),
            TagProvider.tags.take(2),
            Location(-1, "Caesars Forum - Track 1", "Track 1", "DEFCON"),
            listOf(),
            false
        )
    )
}