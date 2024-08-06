package com.advice.analytics.core

import android.os.Bundle
import com.advice.core.local.Conference
import com.advice.core.utils.Storage
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig


class AnalyticsProvider(
    private val storage: Storage,
    private val version: Int,
) {

    private val analytics: FirebaseAnalytics = Firebase.analytics
    private val remoteConfig = Firebase.remoteConfig

    private val isAnalyticsDisabled: Boolean
        get() = !storage.allowAnalytics

    private var isWifiEnabled: Boolean = false

    init {
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            if (it.isSuccessful) {
                val version = remoteConfig.getLong("wifi_min_version")
                isWifiEnabled = isEnabled(version)
            }
        }
    }

    private fun isEnabled(minVersion: Long): Boolean {
        return version == 1 || version >= minVersion
    }

    fun onConferenceChangeEvent(conference: Conference) {
        if (isAnalyticsDisabled)
            return

        analytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.ITEM_ID, conference.id)
            param(FirebaseAnalytics.Param.ITEM_NAME, conference.name)
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "conference")
        }
    }

    fun onVersionClickEvent() {
        if (isAnalyticsDisabled)
            return

        analytics.logEvent("version_click") {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, "easter egg")
        }
    }

    fun setUserProperty(key: String, value: String) {
        if (isAnalyticsDisabled)
            return

        analytics.setUserProperty(key, value)
    }

    fun logEvent(event: String, params: Bundle) {
        if (isAnalyticsDisabled)
            return

        analytics.logEvent(event, params)
    }

    fun onDestinationChanged(route: String) {
        if (isAnalyticsDisabled)
            return

        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, route)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, "MainActivity")
        }
    }

    fun isWifiEnabled(): Boolean {
        return isWifiEnabled
    }
}
