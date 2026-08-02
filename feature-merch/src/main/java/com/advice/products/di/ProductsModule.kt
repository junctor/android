package com.advice.products.di

import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.viewmodel.ProductCart
import com.advice.products.presentation.viewmodel.ProductsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val productsModule =
    module {
        single { ProductsRepository(get(), get()) }
        single<ProductCart> { ProductCart() }
        viewModel { ProductsViewModel() }
    }
