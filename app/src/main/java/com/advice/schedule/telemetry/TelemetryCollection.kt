package com.advice.schedule.telemetry

import android.content.Context
import com.advice.core.utils.Storage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shortstack.hackertracker.BuildConfig

object TelemetryCollection {
    fun apply(context: Context, storage: Storage) {
        apply(
            context = context,
            allowAnalytics = storage.allowAnalytics,
            allowCrashlytics = storage.allowCrashlytics,
        )
    }

    fun apply(
        context: Context,
        allowAnalytics: Boolean,
        allowCrashlytics: Boolean,
    ) {
        FirebaseAnalytics.getInstance(context)
            .setAnalyticsCollectionEnabled(allowAnalytics)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = allowCrashlytics && !BuildConfig.DEBUG
    }
}
