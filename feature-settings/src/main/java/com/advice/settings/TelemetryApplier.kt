package com.advice.settings

import android.content.Context
import com.advice.data.storage.UserPreferencesStore

fun interface TelemetryApplier {
    fun apply(
        context: Context,
        preferences: UserPreferencesStore,
    )
}
