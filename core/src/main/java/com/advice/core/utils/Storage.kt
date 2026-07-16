package com.advice.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.advice.core.local.products.ProductVariantSelection
import com.advice.core.network.CachedFeedbackRequest
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber
import androidx.core.content.edit

class Storage(
    context: Context,
    private val gson: Gson,
    val versionCode: Int,
) {

    companion object {
        const val KEY_PREFERENCES = "preferences"

        private const val USER_UUID = "user_uuid"
        private const val USER_THEME = "user_theme"
        private const val PREFERRED_CONFERENCE = "preferred_conference"

        const val EASTER_EGGS_ENABLED_KEY = "easter_eggs_enabled"
        const val FILTER_BUTTON_SHOWN = "filter_button_shown"
        const val FORCE_TIME_ZONE_KEY = "force_time_zone"
        const val SHOW_SCHEDULE_BY_DEFAULT = "show_schedule_by_default"
        const val USER_ANALYTICS_KEY = "user_analytics"

        const val LATEST_NEWS_READ = "latest_news_read"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)

    var allowAnalytics: Boolean
        get() = preferences.getBoolean(USER_ANALYTICS_KEY, false)
        set(value) {
            preferences.edit { putBoolean(USER_ANALYTICS_KEY, value) }
        }

    var showFilters: Boolean
        get() = preferences.getBoolean(FILTER_BUTTON_SHOWN, true)
        set(value) {
            preferences.edit { putBoolean(FILTER_BUTTON_SHOWN, value) }
        }

    var forceTimeZone: Boolean
        get() = preferences.getBoolean(FORCE_TIME_ZONE_KEY, true)
        set(value) {
            preferences.edit { putBoolean(FORCE_TIME_ZONE_KEY, value) }
        }

    var showSchedule: Boolean
        get() = preferences.getBoolean(SHOW_SCHEDULE_BY_DEFAULT, false)
        set(value) {
            preferences.edit { putBoolean(SHOW_SCHEDULE_BY_DEFAULT, value) }
        }

    var easterEggs: Boolean
        get() = preferences.getBoolean(EASTER_EGGS_ENABLED_KEY, false)
        set(value) {
            preferences.edit { putBoolean(EASTER_EGGS_ENABLED_KEY, value) }
        }

    var preferredConference: Long
        get() = preferences.getLong(PREFERRED_CONFERENCE, -1L)
        set(value) {
            preferences.edit { putLong(PREFERRED_CONFERENCE, value) }
        }

    var theme: String?
        get() = preferences.getString(USER_THEME, null)
        set(value) {
            preferences.edit { putString(USER_THEME, value) }
        }

    var updateVersion: Int?
        get() = preferences.getInt("update_version", -1)
        set(value) {
            preferences.edit { putInt("update_version", value ?: -1) }
        }

    val userUUID: String
        get() {
            var uuid = preferences.getString(USER_UUID, null)
            if (uuid == null) {
                uuid = java.util.UUID.randomUUID().toString()
                preferences.edit { putString(USER_UUID, uuid) }
            }
            return uuid
        }

    fun markNewsAsRead(code: String?, id: Int) {
        if (code == null)
            return
        preferences.edit { putInt("$LATEST_NEWS_READ-$code", id) }
    }

    fun hasReadNews(code: String?, id: Int): Boolean {
        if (code == null)
            return false
        return preferences.getInt("$LATEST_NEWS_READ-$code", -1) == id
    }

    fun dismissMerchInformation(key: String) {
        return preferences.edit(commit = true) { putBoolean("merch_information_$key", true) }
    }

    fun hasSeenMerchInformation(key: String): Boolean {
        return preferences.getBoolean("merch_information_$key", false)
    }

    fun dismissNotificationPopup() {
        preferences.edit { putBoolean("notification_popup", true) }
    }

    fun hasSeenNotificationPopup(): Boolean {
        return preferences.getBoolean("notification_popup", false)
    }

    fun setContentUpdatedTimestamp(id: Long, timestamp: Long) {
        preferences.edit { putLong("content_updated_timestamp_$id", timestamp) }
    }

    fun getContentUpdatedTimestamp(id: Long): Long {
        return preferences.getLong("content_updated_timestamp_$id", 0)
    }

    fun setSelectedProducts(id: Long, list: List<ProductVariantSelection>) {
        preferences.edit { putString("merch_products_selection_$id", gson.toJson(list)) }
    }

    fun getSelectedProducts(id: Long): List<ProductVariantSelection> {
        val json = preferences.getString("merch_products_selection_$id", null) ?: return emptyList()
        try {
            val list = gson.fromJson<List<ProductVariantSelection>>(
                json,
                object : TypeToken<List<ProductVariantSelection>>() {}.type
            )
            return list
        } catch (ex: Exception) {
            Timber.e("Could not convert stored merch products selection to product variant selection")
            Timber.e(ex)
            return emptyList()
        }
    }

    fun storeFeedbackRequest(cachedFeedbackRequest: CachedFeedbackRequest) {
        val cache = getCachedFeedbackRequest() + cachedFeedbackRequest

        preferences.edit { putString("cached_feedback_requests", gson.toJson(cache)) }
    }

    fun getCachedFeedbackRequest(): List<CachedFeedbackRequest> {
        val json = preferences.getString("cached_feedback_requests", null) ?: return emptyList()
        try {
            val list = gson.fromJson<List<CachedFeedbackRequest>>(
                json,
                object : TypeToken<List<CachedFeedbackRequest>>() {}.type
            )
            return list
        } catch (ex: Exception) {
            Timber.e("Could not convert stored cached feedback request to list")
            Timber.e(ex)
            return emptyList()
        }
    }

    fun removeCachedFeedbackRequest(request: CachedFeedbackRequest) {
        val cache = getCachedFeedbackRequest().toMutableList().remove(request)

        preferences.edit { putString("cached_feedback_requests", gson.toJson(cache)) }
    }
}
