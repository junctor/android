package com.advice.schedule.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.FAQ
import com.advice.core.local.FlowResult
import com.advice.schedule.data.repositories.FAQRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class FAQScreenState {
    object Loading : FAQScreenState()
    data class Error(val error: Exception) : FAQScreenState()
    data class Success(val faqs: List<FAQ>) : FAQScreenState()
}

class FAQViewModel : ViewModel(), KoinComponent {

    private val repository by inject<FAQRepository>()

    private val _state = MutableStateFlow<FAQScreenState>(FAQScreenState.Loading)
    val state: Flow<FAQScreenState> = _state

    init {
        viewModelScope.launch {
            repository.faqs.collect {
                _state.value = when (it) {
                    FlowResult.Loading -> FAQScreenState.Loading
                    is FlowResult.Failure -> FAQScreenState.Error(it.error)
                    is FlowResult.Success -> FAQScreenState.Success(it.value)
                }
            }
        }
    }
}
