package com.advice.documents

import androidx.lifecycle.ViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject

class DocumentsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<DocumentsRepository>()

    val documents = repository.documents
}