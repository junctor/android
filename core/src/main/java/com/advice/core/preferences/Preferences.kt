package com.advice.core.preferences

sealed class Preferences(
    val key: String,
    val title: String,
    val summary: String? = null,
    val summaryOn: String? = null,
    val summaryOff: String? = null,
) {
    data object ConferenceTimeZone : Preferences(
        key = "force_time_zone",
        title = "Events in {timezone}",
        summaryOn = "Using conference time zone",
        summaryOff = "Using device zone",
    )

    data object ShowSchedule : Preferences(
        key = "show_schedule_by_default",
        title = "Show schedule on launch",
        summaryOn = "App will open to schedule",
        summaryOff = "App will open to home",
    )

    data object FabShown : Preferences(
        key = "show_filter",
        title = "Show Schedule filter button",
        summaryOn = "Showing FAB",
        summaryOff = "Swipe over to filters",
    )

    data object AllowAnalytics : Preferences(
        key = "allow_analytics",
        title = "Send anonymous usage statistics",
        summary = "Off by default",
    )

    data object AllowCrashlytics : Preferences(
        key = "allow_crashlytics",
        title = "Send crash reports",
        summary = "Off by default",
    )

    data object EasterEggs : Preferences(
        key = "easter_eggs",
        title = "Easter eggs",
        summary = "???",
    )

    data object GlitchAnimation : Preferences(
        key = "glitch_animation_enabled",
        title = "Glitch logo animation",
        summaryOn = "Animated glitch effect",
        summaryOff = "Static glitch logo",
    )

    companion object {
        val entries =
            listOf(
                GlitchAnimation,
                ConferenceTimeZone,
                ShowSchedule,
                FabShown,
                AllowAnalytics,
                AllowCrashlytics,
                EasterEggs,
            )
    }
}
