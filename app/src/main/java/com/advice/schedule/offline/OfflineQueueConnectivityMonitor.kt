package com.advice.schedule.offline

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.feedback.network.ReportSubmissionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Retries offline feedback/report queues when a validated network becomes available.
 */
class OfflineQueueConnectivityMonitor(
    context: Context,
    private val applicationScope: CoroutineScope,
    private val feedbackRepository: FeedbackSubmissionRepository,
    private val reportRepository: ReportSubmissionRepository,
) {
    private val appContext = context.applicationContext
    private val callback =
        object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                drain()
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                ) {
                    drain()
                }
            }
        }

    private var drainJob: Job? = null
    private var registered = false

    fun start() {
        if (registered) return
        registered = true

        val connectivityManager =
            appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        if (connectivityManager == null) {
            Timber.w("ConnectivityManager unavailable; offline queue will only drain on cold start")
            return
        }

        try {
            val request =
                NetworkRequest
                    .Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build()
            connectivityManager.registerNetworkCallback(request, callback)
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to register network callback for offline queue")
        }
    }

    fun drain() {
        if (drainJob?.isActive == true) return
        drainJob =
            applicationScope.launch {
                try {
                    feedbackRepository.retryCached()
                } catch (ex: Exception) {
                    Timber.e(ex, "Failed to retry cached feedback submissions")
                }
                try {
                    reportRepository.retryCached()
                } catch (ex: Exception) {
                    Timber.e(ex, "Failed to retry cached report submissions")
                }
            }
    }
}
