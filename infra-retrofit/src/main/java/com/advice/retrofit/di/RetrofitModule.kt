package com.advice.retrofit.di

import com.advice.data.sources.MapsDataSource
import com.advice.retrofit.datasource.RetrofitMapsDataSource
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.dsl.module

val retrofitModule =
    module {
        single(createdAtStart = true) {
            RetrofitMapsDataSource(
                get(),
                androidContext().applicationContext.getExternalFilesDir(null),
            )
        } withOptions {
            bind<MapsDataSource>()
            onClose { it?.close() }
        }
    }
