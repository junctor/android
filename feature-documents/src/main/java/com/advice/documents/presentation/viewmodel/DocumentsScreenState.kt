package com.advice.documents.presentation.viewmodel

import com.advice.core.local.Document

sealed class DocumentsScreenState {
    object Loading : DocumentsScreenState()
    object Error : DocumentsScreenState()
    data class Success(val documents: List<Document>) : DocumentsScreenState()
}
