package com.advice.documents.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.advice.documents.data.repositories.DocumentsRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DocumentsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<DocumentsRepository>()

    val documents = repository.documents
}