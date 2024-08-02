package com.advice.documents.presentation.viewmodel

import com.advice.core.local.Document

sealed class DocumentsScreenState {
    data object Loading : DocumentsScreenState()
    data object Error : DocumentsScreenState()
    data class Success(val document: Document) : DocumentsScreenState()
}
