package com.advice.schedule.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.advice.schedule.ui.components.DragAnchors
import kotlinx.coroutines.flow.MutableStateFlow

data class MainViewState(
    val currentAnchor: DragAnchors = DragAnchors.Center,
    val isShown: Boolean = false,
    val alpha: Float = 1f,
)

class MainViewModel : ViewModel() {
    val state = MutableStateFlow(MainViewState())

    fun setAnchor(anchor: DragAnchors) {
        state.value = state.value.copy(
            currentAnchor = anchor,
            isShown = anchor == DragAnchors.Start,
        )
    }
}
