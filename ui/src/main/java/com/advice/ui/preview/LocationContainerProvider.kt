package com.advice.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.advice.core.local.LocationContainer

class LocationContainerProvider : PreviewParameterProvider<LocationContainer> {
    override val values: Sequence<LocationContainer>
        get() = containers.asSequence()

    companion object {
        val containers = listOf(LocationContainer(-1, "Casino", "Casino", "open", 0, listOf()))
    }
}