package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.FlowResult
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
            menuRepository.get().collect {
                _state.value = when (it) {
                    is FlowResult.Failure -> MenuScreenState.Error
                    FlowResult.Loading -> MenuScreenState.Loading
                    is FlowResult.Success -> MenuScreenState.Success(it.value)
                }
            }
        }
    }
}
