package com.advice.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.util.Calendar

class Storage(context: Context, private val gson: Gson) {

    companion object {
        const val KEY_PREFERENCES = "preferences"

        private const val USER_THEME = "user_theme"
        private const val PREFERRED_CONFERENCE = "preferred_conference"

        const val EASTER_EGGS_ENABLED_KEY = "easter_eggs_enabled"
        const val SAFE_MODE_ENABLED = "safe_mode_enabled"
        const val DEVELOPER_THEME_UNLOCKED = "developer_theme_unlocked"
        const val NAV_DRAWER_ON_BACK_KEY = "nav_drawer_on_back"
        const val FILTER_BUTTON_SHOWN = "filter_button_shown"
        const val FORCE_TIME_ZONE_KEY = "force_time_zone"
        const val SHOW_SCHEDULE_BY_DEFAULT = "show_schedule_by_default"
        const val USER_ANALYTICS_KEY = "user_analytics"

        const val TUTORIAL_FILTERS = "tutorial_filters"
        const val TUTORIAL_EVENT_LOCATIONS = "tutorial_event_locations"

        const val LATEST_NEWS_READ = "latest_news_read"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)

    var allowAnalytics: Boolean
        get() = preferences.getBoolean(USER_ANALYTICS_KEY, false)
        set(value) {
            preferences.edit().putBoolean(USER_ANALYTICS_KEY, value).apply()
        }

    var navDrawerOnBack: Boolean
        get() = preferences.getBoolean(NAV_DRAWER_ON_BACK_KEY, false)
        set(value) {
            preferences.edit().putBoolean(NAV_DRAWER_ON_BACK_KEY, value).apply()
        }

    var fabShown: Boolean
        get() = preferences.getBoolean(FILTER_BUTTON_SHOWN, true)
        set(value) {
            preferences.edit().putBoolean(FILTER_BUTTON_SHOWN, value).apply()
        }

    var forceTimeZone: Boolean
        get() = preferences.getBoolean(FORCE_TIME_ZONE_KEY, true)
        set(value) {
            preferences.edit().putBoolean(FORCE_TIME_ZONE_KEY, value).apply()
        }

    var showSchedule: Boolean
        get() = preferences.getBoolean(SHOW_SCHEDULE_BY_DEFAULT, false)
        set(value) {
            preferences.edit().putBoolean(SHOW_SCHEDULE_BY_DEFAULT, value).apply()
        }

    var easterEggs: Boolean
        get() = preferences.getBoolean(EASTER_EGGS_ENABLED_KEY, false)
        set(value) {
            preferences.edit().putBoolean(EASTER_EGGS_ENABLED_KEY, value).apply()
        }

    var tutorialFilters: Boolean
        get() = preferences.getBoolean(TUTORIAL_FILTERS, true)
        set(value) {
            preferences.edit().putBoolean(TUTORIAL_FILTERS, value).apply()
        }

    var tutorialEventLocations: Boolean
        get() = preferences.getBoolean(TUTORIAL_EVENT_LOCATIONS, true)
        set(value) {
            preferences.edit().putBoolean(TUTORIAL_EVENT_LOCATIONS, value).apply()
        }

    var preferredConference: Long
        get() = preferences.getLong(PREFERRED_CONFERENCE, -1L)
        set(value) {
            preferences.edit().putLong(PREFERRED_CONFERENCE, value).apply()
        }

    var theme: String?
        get() = preferences.getString(USER_THEME, null)
        set(value) {
            preferences.edit().putString(USER_THEME, value).apply()
        }

    var updateVersion: Int?
        get() = preferences.getInt("update_version", -1)
        set(value) {
            preferences.edit().putInt("update_version", value ?: -1).apply()
        }

    fun setPreference(key: String, isChecked: Boolean) {
        when (key) {
            USER_ANALYTICS_KEY -> allowAnalytics = isChecked
            NAV_DRAWER_ON_BACK_KEY -> navDrawerOnBack = isChecked
            FORCE_TIME_ZONE_KEY -> forceTimeZone = isChecked
            EASTER_EGGS_ENABLED_KEY -> easterEggs = isChecked
            else -> preferences.edit().putBoolean(key, isChecked).apply()
        }
    }

    fun getPreference(key: String, defaultValue: Boolean): Boolean {
        return when (key) {
            USER_ANALYTICS_KEY -> allowAnalytics
            NAV_DRAWER_ON_BACK_KEY -> navDrawerOnBack
            FORCE_TIME_ZONE_KEY -> forceTimeZone
            EASTER_EGGS_ENABLED_KEY -> easterEggs
            else -> preferences.getBoolean(key, defaultValue)
        }
    }

    fun markNewsAsRead(code: String?, id: Int) {
        if (code == null)
            return
        preferences.edit().putInt("$LATEST_NEWS_READ-$code", id).apply()
    }

    fun hasReadNews(code: String?, id: Int): Boolean {
        if (code == null)
            return false
        return preferences.getInt("$LATEST_NEWS_READ-$code", -1) == id
    }

    fun dismissMerchInformation(): Boolean {
        return preferences.edit().putBoolean("merch_information", true).commit()
    }

    fun hasSeenMerchInformation(): Boolean {
        return preferences.getBoolean("merch_information", false)
    }

    object CorruptionLevel {
        const val NONE = 0
        const val MINOR = 1
        const val MEDIUM = 2
        const val MAJOR = 3
    }

    val corruption: Int
        get() {
            if (!getPreference(
                    EASTER_EGGS_ENABLED_KEY,
                    false
                )
            ) {
                return CorruptionLevel.NONE
            }

            val calendar = Calendar.getInstance()
            calendar.time = Time.now()

            val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
            if (dayOfYear < 219)
                return CorruptionLevel.NONE

            return when (dayOfYear) {
                219 -> CorruptionLevel.MINOR
                220 -> CorruptionLevel.MEDIUM
                221 -> CorruptionLevel.MAJOR
                else -> CorruptionLevel.MEDIUM
            }
        }
}
