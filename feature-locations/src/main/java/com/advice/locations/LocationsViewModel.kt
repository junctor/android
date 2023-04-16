package com.advice.locations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Location
import com.advice.core.local.LocationRow
import com.advice.core.ui.LocationsScreenState
import com.advice.locations.data.LocationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

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