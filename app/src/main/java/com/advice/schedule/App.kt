package com.advice.schedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.advice.schedule.di.appModule
import com.google.firebase.FirebaseApp
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@App)
            modules(appModule)
        }


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
