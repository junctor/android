package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Event

class FakeEventProvider : PreviewParameterProvider<Event> {
    override val values: Sequence<Event>
        get() = events.asSequence()

    companion object {
        val events = FakeContentProvider.content.map {
            Event(it, it.sessions.first())
        }
    }
}
