package com.advice.faq.di

import com.advice.faq.data.repositories.FAQRepository
import com.advice.faq.presentation.viewmodel.FAQViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val faqModule =
    module {
        single { FAQRepository(get()) }
        viewModel { FAQViewModel(get()) }
    }
