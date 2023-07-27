package com.advice.locations.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.LocationRow
import com.advice.core.local.LocationStatus

class LocationRowProvider : PreviewParameterProvider<LocationRow> {
    override val values: Sequence<LocationRow>
        get() = containers.asSequence()

    companion object {
        val containers =
            listOf(
                LocationRow(
                    0, "Casino", LocationStatus.Open, 0,
                    hasChildren = true,
                    isExpanded = true,
                    schedule = emptyList(),
                )
            )
    }
}
