package com.advice.products.di

import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.viewmodel.ProductCart
import com.advice.products.presentation.viewmodel.ProductsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val PRODUCTS_VERSION_CODE = "products_version_code"

fun productsModule(versionCode: Int) =
    module {
        single { ProductsRepository(get(), get()) }
        single<ProductCart> { ProductCart() }
        single(named(PRODUCTS_VERSION_CODE)) { versionCode }
        viewModel { ProductsViewModel() }
    }
