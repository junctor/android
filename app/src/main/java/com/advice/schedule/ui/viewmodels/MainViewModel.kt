package com.advice.schedule.ui.viewmodels

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.network.NetworkResponse
import com.advice.core.storage.OfflineQueueStore
import com.advice.core.storage.UserPreferencesStore
import com.advice.core.utils.ToastManager
import com.advice.data.session.UserSession
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.firebase.extensions.documentCacheReads
import com.advice.firebase.extensions.documentReads
import com.advice.firebase.extensions.listenersCount
import com.advice.play.AppManager
import com.advice.schedule.ui.components.DragAnchors
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val userSession: UserSession,
    private val appManager: AppManager,
    private val analytics: AnalyticsProvider,
    private val preferences: UserPreferencesStore,
    private val offlineQueue: OfflineQueueStore,
    private val documentRepository: DocumentsRepository,
    private val feedbackRepository: FeedbackSubmissionRepository,
    private val reportRepository: ReportSubmissionRepository,
    private val toastManager: ToastManager,
) : ViewModel() {
    private val _state = MutableStateFlow(MainViewState())
    val state: Flow<MainViewState> = _state

    init {
        // Showing the Schedule by default if they have enabled this preference
        if (preferences.showSchedule) {
            setAnchor(DragAnchors.Center)
        } else {
            setAnchor(DragAnchors.Start)
        }

        // Attempting to submit any feedback that previously failed
        viewModelScope.launch {
            val cachedFeedbackRequests = offlineQueue.getCachedFeedbackRequest()
            for (request in cachedFeedbackRequests) {
                val response =
                    feedbackRepository.submitFeedback(request.contentId, request.feedbackForm)
                if (response is NetworkResponse.Success) {
                    offlineQueue.removeCachedFeedbackRequest(request)
                }
            }
        }

        // Attempting to submit any reports that previously failed
        viewModelScope.launch {
            try {
                reportRepository.retryCached()
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to retry cached report submissions")
            }
        }

        viewModelScope.launch {
            // Any time the current Conference changes, update the emergency document id
            userSession.getConference().collect {
                val emergencyDocumentId = it.emergencyDocumentId
                val document =
                    if (emergencyDocumentId != null) {
                        documentRepository.get(emergencyDocumentId)
                    } else {
                        null
                    }
                _state.value =
                    _state.value.copy(
                        emergencyDocument = document,
                    )
            }
        }
    }

    fun setAnchor(anchor: DragAnchors) {
        _state.value =
            _state.value.copy(
                currentAnchor = anchor,
                // Only the home panel shows the bottom nav; schedule/filter minimize it.
                isShown = anchor == DragAnchors.Start,
            )
    }

    fun hasSeenNotificationPopup(): Boolean = preferences.hasSeenNotificationPopup()

    fun eventReminderMinutes(): Int = preferences.eventReminderMinutes

    fun showPermissionDialog() {
        _state.value =
            _state.value.copy(
                permissionDialog = true,
            )
    }

    fun dismissPermissionDialog() {
        preferences.dismissNotificationPopup()
        _state.value =
            _state.value.copy(
                permissionDialog = false,
            )
    }

    private var hasStarted = false

    fun onAppStart(
        activity: Activity,
        appUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        if (hasStarted) return
        hasStarted = true

        // Play Age Signals 0.0.4 requires an Activity for the sharing-access prompt.
        userSession.resolveAudienceContext(activity)

        // Only showing the prompt once per version.
        if (preferences.updateVersion != BuildConfig.VERSION_CODE) {
            appManager.checkForUpdate(appUpdateLauncher)
        }
        val format =
            if (android.text.format.DateFormat
                    .is24HourFormat(activity)
            ) {
                "24h"
            } else {
                "12h"
            }
        analytics.setUserProperty("time_format", format)

        viewModelScope.launch {
            toastManager.messages.collect {
                if (it != null) {
                    Toast.makeText(activity, it.text, it.duration).show()
                    toastManager.clear()
                }
            }
        }
    }

    fun onLinkOpen(url: String) {
        Timber.i("Opening link: $url")
        analytics.logEvent(
            "open_link",
            Bundle().apply {
                putString("url", url)
            },
        )
    }

    fun onPause() {
        with(analytics) {
            logEvent(
                "session_document_read",
                Bundle().apply {
                    putInt("total_document_reads", documentReads)
                    putInt("total_document_cache_reads", documentCacheReads)
                    putInt("total_listeners_count", listenersCount)
                },
            )
            documentReads = 0
            documentCacheReads = 0
            listenersCount = 0
        }
    }

    fun onPermissionRequest() {
        analytics.logEvent(
            "request_permission",
            Bundle().apply {
                putString("permission", "POST_NOTIFICATIONS")
            },
        )
    }

    fun onAppUpdateRequest(resultCode: Int) {
        Timber.e("Update flow failed! Result code: $resultCode")
        // Storing the version code so we don't keep asking for updates.
        preferences.updateVersion = BuildConfig.VERSION_CODE
    }

    fun onDestinationChanged(
        navDestination: NavDestination,
        args: Bundle?,
    ) {
        var route =
            navDestination.route
                ?.replace("/{label}", "")
                ?.replace("//", "/") ?: return

        args?.keySet()?.forEach {
            if (route.contains("{$it}")) {
                val value = args.stringValue(it)
                if (value != null) {
                    route = route.replace("{$it}", value)
                }
            }
        }

        Timber.i("navigating to: $route")
        analytics.onDestinationChanged(route)
    }
}

private fun Bundle.stringValue(key: String): String? {
    if (!containsKey(key)) return null
    getString(key)?.let { return it }

    // Dual-default checks: typed getters return the default for missing/wrong types.
    val intA = getInt(key, Int.MIN_VALUE)
    val intB = getInt(key, Int.MAX_VALUE)
    if (intA == intB) return intA.toString()

    val longA = getLong(key, Long.MIN_VALUE)
    val longB = getLong(key, Long.MAX_VALUE)
    if (longA == longB) return longA.toString()

    val floatA = getFloat(key, Float.NEGATIVE_INFINITY)
    val floatB = getFloat(key, Float.POSITIVE_INFINITY)
    if (floatA == floatB) return floatA.toString()

    val doubleA = getDouble(key, Double.NEGATIVE_INFINITY)
    val doubleB = getDouble(key, Double.POSITIVE_INFINITY)
    if (doubleA == doubleB) return doubleA.toString()

    // Remaining nav-arg keys with containsKey true are typically booleans.
    return getBoolean(key).toString()
}
