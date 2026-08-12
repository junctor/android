package com.advice.schedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.advice.data.storage.UserPreferencesStore
import com.advice.schedule.di.appModules
import com.advice.schedule.telemetry.ImageLoadReporter
import com.advice.schedule.telemetry.TelemetryCollection
import com.advice.ui.utils.ImageLoadTelemetry
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        FirebaseApp.initializeApp(this)

        startKoin {
            androidContext(this@App)
            workManagerFactory()
            modules(appModules())
        }

        TelemetryCollection.apply(this, get<UserPreferencesStore>())

        val imageLoadReporter = ImageLoadReporter(get<FirebaseCrashlytics>())
        ImageLoadTelemetry.reporter = imageLoadReporter::report

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
