package com.advice.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ContentSyncStore(
    context: Context,
) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(UserPreferencesStore.KEY_PREFERENCES, Context.MODE_PRIVATE)

    fun setContentUpdatedTimestamp(
        id: Long,
        timestamp: Long,
    ) {
        preferences.edit { putLong("content_updated_timestamp_$id", timestamp) }
    }

    fun getContentUpdatedTimestamp(id: Long): Long = preferences.getLong("content_updated_timestamp_$id", 0)
}
