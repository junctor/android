package com.advice.organizations.di

import com.advice.organizations.data.repositories.OrganizationsRepository
import com.advice.organizations.presentation.viewmodel.OrganizationViewModel
import com.advice.organizations.presentation.viewmodel.OrganizationsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val organizationsModule =
    module {
        single { OrganizationsRepository(get()) }
        viewModel { OrganizationsViewModel(get()) }
        viewModel { OrganizationViewModel(get()) }
    }
