package com.advice.schedule.data.repositories

import android.content.Context
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.ScheduleDayFormat
import com.advice.core.preferences.Preferences
import com.advice.core.storage.UserPreferencesStore
import com.advice.core.ui.SettingsScreenState
import com.advice.data.session.UserSession
import com.advice.schedule.domain.ContentBookmarkUseCase
import com.advice.schedule.telemetry.TelemetryCollection
import kotlinx.coroutines.flow.combine

class SettingsRepository(
    userSession: UserSession,
    private val preferences: UserPreferencesStore,
    private val version: String,
    private val context: Context,
    private val contentBookmarkUseCase: ContentBookmarkUseCase,
) {
    val state =
        combine(
            userSession.getConference(),
            preferences.scheduleDayFormatFlow,
            preferences.eventReminderMinutesFlow,
            preferences.feedbackReminderMinutesFlow,
        ) { conference, scheduleDayFormat, eventReminderMinutes, feedbackReminderMinutes ->
            SettingsScreenState(
                conference.timezone,
                version,
                preferences.forceTimeZone,
                preferences.showSchedule,
                preferences.showFilters,
                preferences.easterEggs,
                preferences.allowAnalytics,
                preferences.allowCrashlytics,
                preferences.glitchAnimationEnabled,
                scheduleDayFormat.id,
                eventReminderMinutes,
                feedbackReminderMinutes,
            )
        }

    fun onPreferenceChanged(
        id: String,
        checked: Boolean,
    ) {
        when (id) {
            Preferences.ConferenceTimeZone.key -> preferences.forceTimeZone = checked
            Preferences.ShowSchedule.key -> preferences.showSchedule = checked
            Preferences.FabShown.key -> preferences.showFilters = checked
            Preferences.AllowAnalytics.key -> {
                preferences.allowAnalytics = checked
                TelemetryCollection.apply(context, preferences)
            }
            Preferences.AllowCrashlytics.key -> {
                preferences.allowCrashlytics = checked
                TelemetryCollection.apply(context, preferences)
            }
            Preferences.EasterEggs.key -> preferences.easterEggs = checked
            Preferences.GlitchAnimation.key -> preferences.glitchAnimationEnabled = checked
        }
    }

    fun onThemeChanged(theme: String): Boolean {
        val result = preferences.theme != theme
        preferences.theme = theme
        return result
    }

    fun onScheduleDayFormatChanged(formatId: String) {
        preferences.scheduleDayFormat = ScheduleDayFormat.fromId(formatId)
    }

    fun onEventReminderMinutesChanged(minutes: Int) {
        val sanitized = ReminderMinutes.sanitizeEvent(minutes)
        if (preferences.eventReminderMinutes == sanitized) {
            return
        }
        preferences.eventReminderMinutes = sanitized
        contentBookmarkUseCase.rescheduleBookmarkedReminders()
    }

    fun onFeedbackReminderMinutesChanged(minutes: Int) {
        val sanitized = ReminderMinutes.sanitizeFeedback(minutes)
        if (preferences.feedbackReminderMinutes == sanitized) {
            return
        }
        preferences.feedbackReminderMinutes = sanitized
        contentBookmarkUseCase.rescheduleBookmarkedReminders()
    }
}
