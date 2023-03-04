package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.Location

class LocationProvider : PreviewParameterProvider<Location> {
    override val values: Sequence<Location>
        get() = locations.asSequence()

    companion object {
        val locations = listOf(
            Location(1, "Casino - Track 1", "Track 1", null, "DEFCON"),
            Location(2, "Casino - Track 2", "Track 2", null, "DEFCON"),
            Location(3, "Casino - Track 3", "Track 3", null, "DEFCON"),
        )
    }
}