package com.advice.schedule.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.advice.schedule.ui.components.DragAnchors
import kotlinx.coroutines.flow.MutableStateFlow

class MainViewModel : ViewModel() {

    val currentAnchor = MutableStateFlow(DragAnchors.Center)

    fun setAnchor(anchor: DragAnchors) {
        currentAnchor.value = anchor
    }
}