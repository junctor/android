package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Event
import com.advice.core.local.Location
import com.advice.core.local.Session
import java.time.Instant

class FakeEventProvider : PreviewParameterProvider<Event> {
    override val values: Sequence<Event>
        get() = events.asSequence()

    val events =
        listOf(
            Event(
                0,
                "DEFCON",
                "Payment Hacking Challenge",
                "Try yourself in ATM, Online bank, POST and Cards hacking challenges.\nPlease join the DEF CON Discord and see the #payv-labs-text channel for more information.",
                Session(
                    id = 1,
                    "America/Los_Angeles",
                    Instant.now(),
                    Instant.now(),
                    Location(-1, "Caesars Forum - Track 1", "Track 1", "DEFCON"),
                ),
                Instant.now(),
                SpeakerProvider.speakers.take(2),
                TagProvider.tags.take(2),
                listOf(),
                false,
            ),
        )
}
