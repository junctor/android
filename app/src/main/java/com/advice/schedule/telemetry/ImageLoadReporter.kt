package com.advice.schedule.telemetry

import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

/**
 * Reports Coil image load failures to Crashlytics as non-fatals. Deduplicates by URL and
 * caps reports per process so a device with a broken network path (e.g. TLS interception
 * rejected by the restricted [com.advice.data.network.Network.client]) surfaces clearly
 * without flooding Crashlytics while scrolling image-heavy lists.
 */
class ImageLoadReporter(
    private val crashlytics: FirebaseCrashlytics,
    private val maxReports: Int = MAX_REPORTS,
) {
    private val reportedUrls = mutableSetOf<String>()

    @Synchronized
    fun report(
        url: String,
        error: Throwable,
    ) {
        Timber.e(error, "Image load failed: %s", url)
        if (reportedUrls.size >= maxReports || !reportedUrls.add(url)) {
            return
        }
        crashlytics.log("Image load failed: $url")
        crashlytics.recordException(error)
    }

    companion object {
        private const val MAX_REPORTS = 10
    }
}
