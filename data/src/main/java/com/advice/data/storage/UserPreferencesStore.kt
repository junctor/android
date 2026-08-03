package com.advice.data.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.ScheduleDayFormat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserPreferencesStore(
    context: Context,
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
        const val USER_CRASHLYTICS_KEY = "user_crashlytics"
        const val GLITCH_ANIMATION_ENABLED_KEY = "glitch_animation_enabled"
        const val SCHEDULE_DAY_FORMAT_KEY = "schedule_day_format"
        const val EVENT_REMINDER_MINUTES_KEY = "event_reminder_minutes"
        const val FEEDBACK_REMINDER_MINUTES_KEY = "feedback_reminder_minutes"

        const val LATEST_NEWS_READ = "latest_news_read"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(KEY_PREFERENCES, Context.MODE_PRIVATE)

    private val _scheduleDayFormat =
        MutableStateFlow(
            ScheduleDayFormat.fromId(
                preferences.getString(SCHEDULE_DAY_FORMAT_KEY, ScheduleDayFormat.MonthDay.id),
            ),
        )
    val scheduleDayFormatFlow: StateFlow<ScheduleDayFormat> = _scheduleDayFormat.asStateFlow()

    private val _eventReminderMinutes =
        MutableStateFlow(
            ReminderMinutes.sanitizeEvent(
                preferences.getInt(EVENT_REMINDER_MINUTES_KEY, ReminderMinutes.DEFAULT),
            ),
        )
    val eventReminderMinutesFlow: StateFlow<Int> = _eventReminderMinutes.asStateFlow()

    private val _feedbackReminderMinutes =
        MutableStateFlow(
            ReminderMinutes.sanitizeFeedback(
                preferences.getInt(FEEDBACK_REMINDER_MINUTES_KEY, ReminderMinutes.DEFAULT),
            ),
        )
    val feedbackReminderMinutesFlow: StateFlow<Int> = _feedbackReminderMinutes.asStateFlow()

    var allowAnalytics: Boolean
        get() = preferences.getBoolean(USER_ANALYTICS_KEY, false)
        set(value) {
            preferences.edit { putBoolean(USER_ANALYTICS_KEY, value) }
        }

    var allowCrashlytics: Boolean
        get() = preferences.getBoolean(USER_CRASHLYTICS_KEY, false)
        set(value) {
            preferences.edit { putBoolean(USER_CRASHLYTICS_KEY, value) }
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

    /** When false, glitch logos use the static `logo_glitch` asset instead of the live effect. */
    var glitchAnimationEnabled: Boolean
        get() = preferences.getBoolean(GLITCH_ANIMATION_ENABLED_KEY, true)
        set(value) {
            preferences.edit { putBoolean(GLITCH_ANIMATION_ENABLED_KEY, value) }
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

    var scheduleDayFormat: ScheduleDayFormat
        get() = _scheduleDayFormat.value
        set(value) {
            preferences.edit { putString(SCHEDULE_DAY_FORMAT_KEY, value.id) }
            _scheduleDayFormat.value = value
        }

    var eventReminderMinutes: Int
        get() = _eventReminderMinutes.value
        set(value) {
            val sanitized = ReminderMinutes.sanitizeEvent(value)
            preferences.edit { putInt(EVENT_REMINDER_MINUTES_KEY, sanitized) }
            _eventReminderMinutes.value = sanitized
        }

    var feedbackReminderMinutes: Int
        get() = _feedbackReminderMinutes.value
        set(value) {
            val sanitized = ReminderMinutes.sanitizeFeedback(value)
            preferences.edit { putInt(FEEDBACK_REMINDER_MINUTES_KEY, sanitized) }
            _feedbackReminderMinutes.value = sanitized
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
                uuid =
                    java.util.UUID
                        .randomUUID()
                        .toString()
                preferences.edit { putString(USER_UUID, uuid) }
            }
            return uuid
        }

    fun markNewsAsRead(
        code: String?,
        id: Int,
    ) {
        if (code == null) {
            return
        }
        preferences.edit { putInt("$LATEST_NEWS_READ-$code", id) }
    }

    fun hasReadNews(
        code: String?,
        id: Int,
    ): Boolean {
        if (code == null) {
            return false
        }
        return preferences.getInt("$LATEST_NEWS_READ-$code", -1) == id
    }

    fun dismissNotificationPopup() {
        preferences.edit { putBoolean("notification_popup", true) }
    }

    fun hasSeenNotificationPopup(): Boolean = preferences.getBoolean("notification_popup", false)
}
