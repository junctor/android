package com.advice.schedule.telemetry

import android.content.Context
import com.advice.core.storage.UserPreferencesStore
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.shortstack.hackertracker.BuildConfig

object TelemetryCollection {
    fun apply(
        context: Context,
        preferences: UserPreferencesStore,
    ) {
        apply(
            context = context,
            allowAnalytics = preferences.allowAnalytics,
            allowCrashlytics = preferences.allowCrashlytics,
        )
    }

    fun apply(
        context: Context,
        allowAnalytics: Boolean,
        allowCrashlytics: Boolean,
    ) {
        FirebaseAnalytics
            .getInstance(context)
            .setAnalyticsCollectionEnabled(allowAnalytics)
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = allowCrashlytics && !BuildConfig.DEBUG
    }
}
