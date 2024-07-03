package com.advice.documents.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.documents.data.repositories.DocumentsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DocumentsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<DocumentsRepository>()

    private val _state = MutableStateFlow<DocumentsScreenState>(DocumentsScreenState.Loading)
    val state: Flow<DocumentsScreenState> = _state

    init {
        viewModelScope.launch {
            repository.documents.collect {
                _state.value = if (it.isEmpty()) DocumentsScreenState.Error else DocumentsScreenState.Success(it)
            }
        }
    }
}
