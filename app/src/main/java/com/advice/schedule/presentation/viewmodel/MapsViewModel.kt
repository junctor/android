package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.schedule.data.repositories.MapRepository
import com.advice.ui.states.MapsScreenState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class MapsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<MapRepository>()

    private val _state = MutableStateFlow<MapsScreenState>(MapsScreenState.Loading)
    val state: Flow<MapsScreenState> = _state

    init {
        viewModelScope.launch {
            repository.maps.collect {
                if (it.isEmpty()) {
                    _state.value = MapsScreenState.Error("No maps found")
                } else {
                    _state.value = MapsScreenState.Success(it)
                }
            }
        }
    }
}
