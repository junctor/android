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

    fun get(id: Long?) {
        if (id == null) {
            _state.value = DocumentsScreenState.Error
            return
        }

        viewModelScope.launch {
            _state.value = DocumentsScreenState.Loading
            val document = repository.get(id)
            if (document != null) {
                _state.value = DocumentsScreenState.Success(document)
            } else {
                _state.value = DocumentsScreenState.Error
            }
        }
    }
}
