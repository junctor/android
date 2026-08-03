package com.advice.locations.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.LocationRow
import com.advice.locations.data.repositories.LocationsRepository
import com.advice.locations.presentation.state.LocationsScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class LocationsViewModel(
    private val repository: LocationsRepository,
) : ViewModel() {
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
