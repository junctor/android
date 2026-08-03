package com.advice.faq.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.FAQ
import com.advice.core.local.FlowResult
import com.advice.faq.data.repositories.FAQRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

sealed class FAQScreenState {
    object Loading : FAQScreenState()

    data class Error(
        val error: Exception,
    ) : FAQScreenState()

    data class Success(
        val faqs: List<FAQ>,
    ) : FAQScreenState()
}

class FAQViewModel(
    private val repository: FAQRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<FAQScreenState>(FAQScreenState.Loading)
    val state: Flow<FAQScreenState> = _state

    init {
        viewModelScope.launch {
            repository.faqs.collect {
                _state.value =
                    when (it) {
                        FlowResult.Loading -> FAQScreenState.Loading
                        is FlowResult.Failure -> FAQScreenState.Error(it.error)
                        is FlowResult.Success -> FAQScreenState.Success(it.value)
                    }
            }
        }
    }
}
