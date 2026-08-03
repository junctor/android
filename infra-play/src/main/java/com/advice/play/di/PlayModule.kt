package com.advice.play.di

import com.advice.play.AgeSignalsRepository
import com.advice.play.AppManager
import com.advice.play.createAgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val playModule =
    module {
        single { AppManager(androidContext()) }
        single<AgeSignalsManager> {
            createAgeSignalsManager(androidContext())
        }
        single { AgeSignalsRepository(get(), get()) }
    }
