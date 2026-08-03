package com.advice.merch.di

import com.advice.merch.data.repositories.ProductsRepository
import com.advice.merch.presentation.viewmodel.ProductCart
import com.advice.merch.presentation.viewmodel.ProductsViewModel
import com.advice.merch.storage.MerchCartStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val MERCH_VERSION_CODE = "merch_version_code"

fun merchModule(versionCode: Int) =
    module {
        single { MerchCartStore(androidContext(), get()) }
        single { ProductsRepository(get(), get()) }
        single(named(MERCH_VERSION_CODE)) { versionCode }
        viewModel {
            ProductsViewModel(
                get(),
                get(),
                ProductCart(),
                get(named(MERCH_VERSION_CODE)),
                get(),
            )
        }
    }
