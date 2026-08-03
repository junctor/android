package com.advice.menu.di

import com.advice.menu.data.repositories.MenuRepository
import com.advice.menu.presentation.viewmodel.MenuViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val menuModule =
    module {
        single { MenuRepository(get(), get()) }
        viewModel { MenuViewModel(get()) }
    }
