package com.advice.documents.di

import com.advice.documents.data.repositories.DocumentsRepository
import org.koin.dsl.module

val documentsModule =
    module {
        single { DocumentsRepository(get()) }
    }
