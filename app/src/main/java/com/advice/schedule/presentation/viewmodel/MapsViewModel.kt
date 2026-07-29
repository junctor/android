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
import org.koin.core.component.get
import timber.log.Timber

class MapsViewModel(
    repository: MapRepository? = null,
) : ViewModel(),
    KoinComponent {
    // Default path uses Koin (navGraphViewModel / NewInstanceFactory); tests pass a repository.
    private val repository: MapRepository = repository ?: get()

    private val _state = MutableStateFlow<MapsScreenState>(MapsScreenState.Loading)
    val state: Flow<MapsScreenState> = _state

    private var conferenceId: Long? = null
    private var selectedMapName: String? = null

    init {
        viewModelScope.launch {
            this@MapsViewModel.repository.maps.collect { result ->
                when (result) {
                    is FlowResult.Failure -> {
                        Timber.e(result.error, "Maps: flow failure")
                        _state.value = MapsScreenState.Error("Error loading maps")
                    }

                    FlowResult.Loading -> {
                        Timber.d("Maps: loading")
                        _state.value = MapsScreenState.Loading
                    }

                    is FlowResult.Success -> {
                        val maps = result.value
                        val newConferenceId = maps.conference.id
                        if (conferenceId != newConferenceId) {
                            Timber.d(
                                "Maps: conference changed %s -> %s (%s)",
                                conferenceId,
                                newConferenceId,
                                maps.conference.name,
                            )
                            conferenceId = newConferenceId
                            selectedMapName = null
                        }

                        if (maps.maps.isEmpty()) {
                            _state.value =
                                MapsScreenState.Error("No maps for ${maps.conference.name}")
                        } else {
                            val selected =
                                selectedMapName
                                    ?.let { name -> maps.maps.find { it.name == name } }
                                    ?: maps.maps.first()
                            selectedMapName = selected.name
                            _state.value = MapsScreenState.Success(selected, maps.maps)
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
            selectedMapName = name
            _state.value = state.copy(file = file)
        }
    }
}
