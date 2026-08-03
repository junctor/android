package com.advice.documents.di

import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.documents.presentation.viewmodel.DocumentsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val documentsModule =
    module {
        single { DocumentsRepository(get()) }
        viewModel { DocumentsViewModel(get()) }
    }
