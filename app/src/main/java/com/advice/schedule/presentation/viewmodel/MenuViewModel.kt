package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.FlowResult
import com.advice.core.local.Menu
import com.advice.schedule.data.repositories.MenuRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class MenuScreenState {
    object Loading : MenuScreenState()

    object Error : MenuScreenState()

    data class Success(
        val menu: List<Menu>,
    ) : MenuScreenState()
}

class MenuViewModel(
    private val menuRepository: MenuRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<MenuScreenState>(MenuScreenState.Loading)
    val state: Flow<MenuScreenState> = _state

    init {
        viewModelScope.launch {
            menuRepository.get().collect {
                _state.value =
                    when (it) {
                        is FlowResult.Failure -> MenuScreenState.Error
                        FlowResult.Loading -> MenuScreenState.Loading
                        is FlowResult.Success -> MenuScreenState.Success(it.value)
                    }
            }
        }
    }
}
