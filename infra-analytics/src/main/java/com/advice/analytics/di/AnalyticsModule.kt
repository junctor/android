package com.advice.analytics.di

import com.advice.analytics.core.AnalyticsProvider
import com.google.firebase.analytics.FirebaseAnalytics
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

fun analyticsModule(versionCode: Int) =
    module {
        single { FirebaseAnalytics.getInstance(androidContext()) }
        single { AnalyticsProvider(get(), versionCode) }
    }
