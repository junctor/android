package com.advice.schedule.presentation.viewmodel

import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.local.FlowResult
import com.advice.data.session.UserSession
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.storage.UserPreferencesStore
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.network.ReportSubmissionRepository
import com.advice.firebase.telemetry.FirestoreTelemetry
import com.advice.play.AppManager
import com.advice.schedule.offline.OfflineQueueConnectivityMonitor
import com.advice.schedule.ui.components.DragAnchors
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

class MainViewModel(
    private val userSession: UserSession,
    private val appManager: AppManager,
    private val analytics: AnalyticsProvider,
    private val preferences: UserPreferencesStore,
    private val documentRepository: DocumentsRepository,
    private val feedbackRepository: FeedbackSubmissionRepository,
    private val reportRepository: ReportSubmissionRepository,
    private val conferencesDataSource: ConferencesDataSource,
    private val offlineQueueConnectivityMonitor: OfflineQueueConnectivityMonitor,
    private val firestoreTelemetry: FirestoreTelemetry,
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

        // Cold-start drain (connectivity monitor also drains when network returns).
        viewModelScope.launch {
            try {
                feedbackRepository.retryCached()
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to retry cached feedback submissions")
            }
        }

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

    /**
     * Switches preferred conference when a document deep link includes `c`.
     * @return true if the conference was found and selected (or already current).
     */
    suspend fun switchConferenceByCode(code: String): Boolean {
        val current = userSession.currentConference
        if (current?.code == code) return true

        val conferences =
            when (val result = conferencesDataSource.get().first()) {
                is FlowResult.Success -> result.value
                else -> emptyList()
            }
        val conference = conferences.find { it.code == code } ?: return false
        userSession.setConference(conference)
        return true
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

    /**
     * One-time app start work. Returns true on the first call so the Activity can run
     * its own Activity-bound first-start work (e.g. Age Signals resolution) once per
     * process rather than on every recreation.
     */
    fun onAppStart(
        is24HourFormat: Boolean,
        appUpdateLauncher: ActivityResultLauncher<IntentSenderRequest>,
    ): Boolean {
        if (hasStarted) return false
        hasStarted = true

        // Only showing the prompt once per version.
        if (preferences.updateVersion != BuildConfig.VERSION_CODE) {
            appManager.checkForUpdate(appUpdateLauncher)
        }
        analytics.setUserProperty("time_format", if (is24HourFormat) "24h" else "12h")

        offlineQueueConnectivityMonitor.start()
        return true
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
        val counts = firestoreTelemetry.snapshotAndReset()
        analytics.logEvent(
            "session_document_read",
            Bundle().apply {
                putInt("total_document_reads", counts.documentReads)
                putInt("total_document_cache_reads", counts.documentCacheReads)
                putInt("total_listeners_count", counts.listenersCount)
            },
        )
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
