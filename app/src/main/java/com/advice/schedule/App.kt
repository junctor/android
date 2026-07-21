package com.advice.schedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.advice.core.utils.Storage
import com.advice.schedule.di.appModule
import com.advice.schedule.telemetry.TelemetryCollection
import com.google.firebase.FirebaseApp
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.android.get
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

        TelemetryCollection.apply(this, get<Storage>())

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
