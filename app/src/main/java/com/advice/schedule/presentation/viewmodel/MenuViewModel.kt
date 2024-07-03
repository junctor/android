package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Menu
import com.advice.schedule.data.repositories.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class MenuScreenState {
    object Loading : MenuScreenState()
    object Error : MenuScreenState()
    data class Success(val menu: List<Menu>) : MenuScreenState()
}

class MenuViewModel : ViewModel(), KoinComponent {

    private val menuRepository by inject<MenuRepository>()

    private val _state = MutableStateFlow<MenuScreenState>(MenuScreenState.Loading)
    val state: Flow<MenuScreenState> = _state

    init {
        viewModelScope.launch {
            menuRepository.menu.collect {
                _state.value =
                    if (it.isEmpty()) MenuScreenState.Error else MenuScreenState.Success(it)
            }
        }
    }
}
