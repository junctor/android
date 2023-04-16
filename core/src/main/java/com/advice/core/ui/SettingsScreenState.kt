package com.advice.core.ui

data class SettingsScreenState(
    val timezone: String,
    val version: String,
    val useConferenceTimeZone: Boolean,
    val showFilterButton: Boolean,
    val enableEasterEggs: Boolean,
    val enableAnalytics: Boolean,
    val showTwitterHandle: Boolean,
)