package com.advice.schedule.ui.viewmodels

import android.net.Uri
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.utils.Storage
import com.advice.firebase.extensions.document_cache_reads
import com.advice.firebase.extensions.document_reads
import com.advice.firebase.extensions.listeners_count
import com.advice.play.AppManager
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.components.DragAnchors
import com.google.firebase.messaging.FirebaseMessaging
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainViewModel : ViewModel(), KoinComponent {

    private val appManager by inject<AppManager>()
    private val analytics by inject<AnalyticsProvider>()
    private val storage by inject<Storage>()

    private val _state = MutableStateFlow(MainViewState())
    val state: Flow<MainViewState> = _state

    init {
        // Showing the Schedule by default if they have enabled this preference
        if (storage.showSchedule) {
            setAnchor(DragAnchors.Center)
        } else {
            setAnchor(DragAnchors.Start)
        }

        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isSuccessful) {
                Timber.d("FCM Token: ${it.result}")
            }
        }
    }

    fun setAnchor(anchor: DragAnchors) {
        _state.value = _state.value.copy(
            currentAnchor = anchor,
            isShown = anchor == DragAnchors.Start,
        )
    }

    fun hasSeenNotificationPopup(): Boolean {
        return storage.hasSeenNotificationPopup()
    }

    fun showPermissionDialog() {
        _state.value = _state.value.copy(
            permissionDialog = true,
        )
    }

    fun dismissPermissionDialog() {
        storage.dismissNotificationPopup()
        _state.value = _state.value.copy(
            permissionDialog = false,
        )
    }

    fun onPermissionResult(granted: Boolean) {
        if (granted) {
            analytics.logEvent(
                "permission_granted", bundleOf(
                    "permission" to "POST_NOTIFICATIONS"
                )
            )
            Timber.i("Permission granted")
        } else {
            analytics.logEvent(
                "permission_denied", bundleOf(
                    "permission" to "POST_NOTIFICATIONS"
                )
            )
            Timber.i("Permission denied")
        }
    }

    fun onAppStart(context: MainActivity) {
        // Only showing the prompt once per version.
        if (storage.updateVersion != BuildConfig.VERSION_CODE) {
            appManager.checkForUpdate(context, MainActivity.REQUEST_CODE_UPDATE)
        }
        val format = if (android.text.format.DateFormat.is24HourFormat(context)) {
            "24h"
        } else {
            "12h"
        }
        analytics.setUserProperty("time_format", format)
    }

    fun onLinkOpen(url: String) {
        Timber.i("Opening link: $url")
        analytics.logEvent("open_link", bundleOf("url" to url))
    }

    fun onPause() {
        with(analytics) {
            logEvent(
                "session_document_read", bundleOf(
                    "total_document_reads" to document_reads,
                    "total_document_cache_reads" to document_cache_reads,
                    "total_listeners_count" to listeners_count,
                )
            )
            document_reads = 0
            document_cache_reads = 0
            listeners_count = 0
        }
    }

    fun onPermissionRequest() {
        analytics.logEvent(
            "request_permission", bundleOf(
                "permission" to "POST_NOTIFICATIONS"
            )
        )
    }

    fun onAppUpdateRequest(resultCode: Int) {
        Timber.e("Update flow failed! Result code: $resultCode")
        // Storing the version code so we don't keep asking for updates.
        storage.updateVersion = BuildConfig.VERSION_CODE
    }

    fun onDestinationChanged(navDestination: NavDestination, args: Bundle?) {
        var route = navDestination.route
            ?.replace("/{label}", "")
            ?.replace("//", "/") ?: return

        args?.keySet()?.forEach {
            val value = args.get(it)
            if (value != null && value is String) {
                route = route.replace("{${it}}", value)
            }
        }

        Timber.i("navigating to: $route")
        analytics.onDestinationChanged(route)
    }

    fun onNewIntent(uri: Uri): Navigation? {
        Timber.i("onNewIntent: $uri")
        analytics.logEvent(
            "open_deep_link",
            bundleOf(
                "uri" to uri.toString()
            )
        )
        val conference = uri.getQueryParameter("c") ?: return null
        val event = uri.getQueryParameter("e") ?: return null
        val (content, session) = if (event.contains(":")) {
            event.split(":")
        } else {
            listOf(event, "")
        }
        return Navigation.Event(conference, content, session)
    }
}
