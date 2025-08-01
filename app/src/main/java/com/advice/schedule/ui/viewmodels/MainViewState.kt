package com.advice.schedule.ui.viewmodels

import com.advice.core.local.Document
import com.advice.schedule.ui.components.DragAnchors

data class MainViewState(
    val currentAnchor: DragAnchors = DragAnchors.Start,
    val isShown: Boolean = false,
    val alpha: Float = 1f,
    val permissionDialog: Boolean = false,
    val emergencyDocument: Document? = null,
)
