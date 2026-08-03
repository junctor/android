package com.advice.search.di

import com.advice.search.data.repositories.SearchRepository
import com.advice.search.presentation.viewmodel.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val searchModule =
    module {
        single { SearchRepository(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { SearchViewModel(get()) }
    }
