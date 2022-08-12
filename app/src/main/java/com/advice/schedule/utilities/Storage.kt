package com.advice.schedule.utilities

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.advice.schedule.models.firebase.FirebaseUser
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.themes.ThemesManager
import com.google.gson.Gson
import java.util.*

class Storage(context: Context, private val gson: Gson) {

    companion object {
        private const val USER_PROFILE = "user_profile"
        private const val USER_THEME = "user_theme"
        private const val PREFERRED_CONFERENCE = "preferred_conference"

        const val EASTER_EGGS_ENABLED_KEY = "easter_eggs_enabled"
        const val SAFE_MODE_ENABLED = "safe_mode_enabled"
        const val DEVELOPER_THEME_UNLOCKED = "developer_theme_unlocked"
        const val NAV_DRAWER_ON_BACK_KEY = "nav_drawer_on_back"
        const val FILTER_BUTTON_SHOWN = "filter_button_shown"
        const val FORCE_TIME_ZONE_KEY = "force_time_zone"
        const val USER_ANALYTICS_KEY = "user_analytics"

        const val TUTORIAL_FILTERS = "tutorial_filters"
        const val TUTORIAL_EVENT_LOCATIONS = "tutorial_event_locations"
    }

    private val preferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    var allowAnalytics: Boolean
        get() = preferences.getBoolean(USER_ANALYTICS_KEY, true)
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

    var theme: ThemesManager.Theme?
        get() = gson.fromJson(
            preferences.getString(USER_THEME, ""),
            ThemesManager.Theme::class.java
        )
        set(value) {
            preferences.edit().putString(USER_THEME, gson.toJson(value)).apply()
        }

    var user: FirebaseUser?
        get() = gson.fromJson(preferences.getString(USER_PROFILE, ""), FirebaseUser::class.java)
        set(value) {
            preferences.edit().putString(USER_PROFILE, gson.toJson(value)).apply()
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
                ) || theme != ThemesManager.Theme.SafeMode
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
