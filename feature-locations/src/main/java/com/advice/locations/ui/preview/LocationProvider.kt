package com.advice.locations.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Location

class LocationProvider : PreviewParameterProvider<Location> {
    override val values: Sequence<Location>
        get() = locations.asSequence()

    companion object {
        val locations = listOf(
            Location(1, "Casino - Track 1", "Track 1", "DEFCON"),
        )
    }
}
