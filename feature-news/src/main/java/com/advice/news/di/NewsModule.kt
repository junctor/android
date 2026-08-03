package com.advice.news.di

import com.advice.news.data.repositories.NewsRepository
import com.advice.news.presentation.viewmodel.NewsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val newsModule =
    module {
        single { NewsRepository(get()) }
        viewModel { NewsViewModel(get()) }
    }
