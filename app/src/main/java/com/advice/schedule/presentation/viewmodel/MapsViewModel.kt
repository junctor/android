package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.FlowResult
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
            repository.maps.collect { result ->
                when (result) {
                    is FlowResult.Failure -> _state.value =
                        MapsScreenState.Error("Error loading maps")

                    FlowResult.Loading -> _state.value = MapsScreenState.Loading
                    is FlowResult.Success -> {
                        result.value.run {
                            if (maps.isEmpty()) {
                                _state.value =
                                    MapsScreenState.Error("No maps for ${conference.name}")
                            } else {
                                _state.value = MapsScreenState.Success(maps.first(), maps)
                            }
                        }
                    }
                }
            }
        }
    }

    fun onMapChanged(name: String) {
        val state = _state.value as? MapsScreenState.Success ?: return
        val file = state.maps.find { it.name == name }
        if (file != null) {
            _state.value = state.copy(file = file)
        }
    }
}
