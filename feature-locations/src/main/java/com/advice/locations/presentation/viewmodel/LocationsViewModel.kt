package com.advice.locations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.LocationRow
import com.advice.core.ui.LocationsScreenState
import com.advice.locations.data.repositories.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<LocationRepository>()

    private val _state = MutableStateFlow(LocationsScreenState(emptyList()))
    val state = _state

    init {
        viewModelScope.launch {
            repository.locations.collect { locations ->
                _state.value = LocationsScreenState(locations)
            }
        }
    }

    fun toggle(location: LocationRow) {
        viewModelScope.launch {
            if (location.isExpanded) {
                repository.collapse(location.id)
            } else {
                repository.expand(location.id)
            }
        }
    }
}
