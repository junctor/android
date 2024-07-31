package com.advice.schedule.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.advice.core.utils.Storage
import com.advice.schedule.ui.components.DragAnchors
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainViewModel : ViewModel(), KoinComponent {

    private val storage by inject<Storage>()

    val state = MutableStateFlow(MainViewState())

    fun setAnchor(anchor: DragAnchors) {
        state.value = state.value.copy(
            currentAnchor = anchor,
            isShown = anchor == DragAnchors.Start,
        )
    }

    fun hasSeenNotificationPopup(): Boolean {
        return storage.hasSeenNotificationPopup()
    }

    fun showPermissionDialog() {
        state.value = state.value.copy(
            permissionDialog = true,
        )
    }

    fun dismissPermissionDialog() {
        storage.dismissNotificationPopup()
        state.value = state.value.copy(
            permissionDialog = false,
        )
    }
}
