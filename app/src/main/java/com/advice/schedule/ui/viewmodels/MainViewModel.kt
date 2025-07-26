package com.advice.schedule.ui.viewmodels

import android.os.Bundle
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavDestination
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.network.NetworkResponse
import com.advice.core.utils.Response
import com.advice.core.utils.Storage
import com.advice.core.utils.ToastManager
import com.advice.data.session.UserSession
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.feedback.network.FeedbackRepository
import com.advice.firebase.extensions.document_cache_reads
import com.advice.firebase.extensions.document_reads
import com.advice.firebase.extensions.listeners_count
import com.advice.play.AppManager
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.components.DragAnchors
import com.google.firebase.messaging.FirebaseMessaging
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainViewModel : ViewModel(), KoinComponent {

    private val userSession by inject<UserSession>()
    private val appManager by inject<AppManager>()
    private val analytics by inject<AnalyticsProvider>()
    private val storage by inject<Storage>()
    private val documentRepository by inject<DocumentsRepository>()
    private val feedbackRepository by inject<FeedbackRepository>()
    private val toastManager by inject<ToastManager>()

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

        // Attempting to submit any feedback that previously failed
        viewModelScope.launch {
            val cachedFeedbackRequests = storage.getCachedFeedbackRequest()
            for (request in cachedFeedbackRequests) {
                val response =
                    feedbackRepository.submitFeedback(request.contentId, request.feedbackForm)
                if (response is NetworkResponse.Success) {
                    storage.removeCachedFeedbackRequest(request)
                }
            }
        }

        viewModelScope.launch {
            // Any time the current Conference changes, update the emergency document id
            userSession.getConference().collect {
                val emergencyDocumentId = it.emergencyDocumentId
                val document = if (emergencyDocumentId != null) {
                    documentRepository.get(emergencyDocumentId)
                } else {
                    null
                }
                _state.value = _state.value.copy(
                    emergencyDocument = document
                )
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

        viewModelScope.launch {
            toastManager.messages.collect {
                if (it != null) {
                    Toast.makeText(context, it.text, it.duration).show()
                    toastManager.clear()
                }
            }
        }
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
}
