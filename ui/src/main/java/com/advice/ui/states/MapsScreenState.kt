package com.advice.ui.states

import com.advice.core.local.MapFile

sealed class MapsScreenState {
    data object Loading : MapsScreenState()

    data class Success(
        val file: MapFile,
        val maps: List<MapFile>,
    ) : MapsScreenState()

    data class Error(
        val message: String,
    ) : MapsScreenState()
}
