package com.advice.ui.states

data class SettingsScreenState(
    val timezone: String,
    val version: String,
    val useConferenceTimeZone: Boolean,
    val showSchedule: Boolean,
    val showFilterButton: Boolean,
    val enableEasterEggs: Boolean,
    val enableAnalytics: Boolean,
    val enableCrashlytics: Boolean,
    val enableGlitchAnimation: Boolean,
    val scheduleDayFormat: String,
    val eventReminderMinutes: Int,
    val feedbackReminderMinutes: Int,
)
