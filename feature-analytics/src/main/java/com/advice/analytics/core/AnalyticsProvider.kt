package com.advice.analytics.core

import com.advice.core.local.Conference
import com.advice.core.local.Event
import com.advice.core.local.Speaker
import com.advice.core.utils.Storage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class AnalyticsProvider(private val storage: Storage) {

    companion object {
        const val SCREEN_HOME = "Home"
        const val SCREEN_SCHEDULE = "Schedule"
        const val SCREEN_FILTERS = "Filters"

        const val SCREEN_EVENT = "Event"
        const val SCREEN_SPEAKER = "Speaker"

        const val SCREEN_MAPS = "Maps"

        const val SCREEN_INFORMATION = "Information"
        const val SCREEN_WIFI = "WiFi"
        const val SCREEN_CODE_OF_CONDUCT = "Code of Conduct"
        const val SCREEN_HELP_AND_SUPPORT = "Help & Support"
        const val SCREEN_FAQ = "FAQ"
        const val SCREEN_LOCATIONS = "Locations"
        const val SCREEN_SPEAKERS = "Speakers"
        const val SCREEN_VENDORS_AND_PARTNERS = "Vendors & Partners"

        const val SCREEN_SETTINGS = "Settings"
    }

    private val analytics: FirebaseAnalytics = Firebase.analytics

    fun onEventView(event: Event) = log {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, event.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, event.title)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "event")
        }
    }

    fun onEventBookmark(event: Event) = log {
        analytics.logEvent("event_bookmark") {
            param(FirebaseAnalytics.Param.ITEM_ID, event.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, event.title)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "event")
            param("is_bookmarked", event.isBookmarked.toString())
        }
    }

    fun onSpeakerView(speaker: Speaker) = log {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, speaker.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, speaker.name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "speaker")
        }
    }

    fun onSpeakerEvent(speaker: Speaker) = log {
        analytics.logEvent("speaker_twitter") {
            param(FirebaseAnalytics.Param.ITEM_ID, speaker.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, speaker.name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "speaker")
        }
    }

    fun onDeveloperEvent(isFollow: Boolean) = log {
        analytics.logEvent("developer_twitter") {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "developer")
            param("is_follow", isFollow.toString())
        }
    }

    fun onConferenceChangeEvent(conference: Conference) {
        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, conference.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, conference.name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "conference")
        }
    }

    fun setScreen(screen: String) = log {
        Timber.d("Current Screen: $screen")
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen)
        }
    }

    fun log(message: String) {
        //todo: Crashlytics.getInstance().core.log(message)
    }

    fun log(block: () -> Unit) {
        // Bypass to track if they're turning analytics off
        if (!storage.allowAnalytics/* && !event.toString().contains(SETTINGS_ANALYTICS)*/) {
            Timber.d("User has disabled analytics, not tracking this event.")
            return
        }
        block()
    }
}
