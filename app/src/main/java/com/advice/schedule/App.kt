package com.advice.schedule

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.di.appModule
import com.advice.schedule.utilities.Storage
import com.github.stkent.amplify.feedback.DefaultEmailFeedbackCollector
import com.github.stkent.amplify.feedback.GooglePlayStoreFeedbackCollector
import com.github.stkent.amplify.tracking.Amplify
import com.google.firebase.FirebaseApp
import com.orhanobut.logger.Logger
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    companion object {
        val isDeveloper = BuildConfig.DEBUG

        lateinit var instance: App
    }


    val storage: Storage by inject()
    val database: DatabaseManager by inject()

    override fun onCreate() {
        super.onCreate()
        instance = this

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        FirebaseApp.initializeApp(this)


        startKoin {
            androidContext(this@App)
            modules(appModule)
        }


        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        initLogger()
        initFeedback()
    }

    private fun initFeedback() {
        Amplify.initSharedInstance(this)
            .setPositiveFeedbackCollectors(GooglePlayStoreFeedbackCollector())
            .setCriticalFeedbackCollectors(DefaultEmailFeedbackCollector(Constants.FEEDBACK_EMAIL))
            .applyAllDefaultRules()
            .setLastUpdateTimeCooldownDays(4)
    }

    private fun initLogger() {
        Logger.init().methodCount(1).hideThreadInfo()
    }
}
