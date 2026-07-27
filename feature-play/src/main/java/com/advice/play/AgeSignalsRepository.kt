@file:Suppress("GrazieInspectionRunner", "GrazieInspectionRunner")

package com.advice.play

import android.app.Activity
import com.advice.core.audience.AudienceContext
import com.advice.core.audience.AudienceStatus
import com.google.android.play.agesignals.AgeSignalsAccessRequest
import com.google.android.play.agesignals.AgeSignalsException
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsRequest
import com.google.android.play.agesignals.AgeSignalsResult
import com.google.android.play.agesignals.model.AgeRangeSource
import com.google.android.play.agesignals.model.AgeSignalsErrorCode
import com.google.android.play.agesignals.model.AgeSignalsStatus
import com.google.android.play.agesignals.model.SignificantChangeStatus
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.time.Duration.Companion.milliseconds

@Suppress("GrazieInspectionRunner", "GrazieInspectionRunner")
class AgeSignalsRepository(
    private val manager: AgeSignalsManager,
    private val crashlytics: FirebaseCrashlytics,
) {

    /**
     * Requests age-signals access (may show Play's sharing prompt), then fetches signals when shared.
     *
     * @param activity required by Play to render the in-app age-sharing prompt when needed
     */
    suspend fun get(
        activity: Activity,
        maxRetries: Int = 3,
    ): AudienceContext {
        var lastException: Exception? = null
        repeat(maxRetries) { attempt ->
            try {
                return fetchAgeSignals(activity)
            } catch (ex: AgeSignalsException) {
                lastException = ex
                if (!isRetryable(ex.errorCode) || attempt >= maxRetries - 1) {
                    return@repeat
                }
                Timber.w(ex, "AgeSignalsRequest retryable error (attempt ${attempt + 1})")
                delay((RETRY_DELAY_MS * (attempt + 1)).milliseconds)
            } catch (ex: Exception) {
                lastException = ex
                Timber.w(ex, "AgeSignalsRequest non-retryable error (attempt ${attempt + 1})")
                return@repeat
            }
        }

        lastException?.let {
            crashlytics.recordException(it)
        }

        return AudienceContext.Unavailable
    }

    private fun isRetryable(errorCode: Int): Boolean {
        return when (errorCode) {
            AgeSignalsErrorCode.API_NOT_AVAILABLE,
            AgeSignalsErrorCode.PLAY_STORE_NOT_FOUND,
            AgeSignalsErrorCode.NETWORK_ERROR,
            AgeSignalsErrorCode.PLAY_SERVICES_NOT_FOUND,
            AgeSignalsErrorCode.CANNOT_BIND_TO_SERVICE,
            AgeSignalsErrorCode.PLAY_STORE_VERSION_OUTDATED,
            AgeSignalsErrorCode.PLAY_SERVICES_VERSION_OUTDATED,
            AgeSignalsErrorCode.CLIENT_TRANSIENT_ERROR,
            -> true
            else -> false
        }
    }

    private suspend fun fetchAgeSignals(activity: Activity): AudienceContext {
        val accessStatus = requestAccess(activity)
        Timber.d("ageSignalsStatus: $accessStatus")

        return when (accessStatus) {
            AgeSignalsStatus.SHARED -> checkSignals()
            AgeSignalsStatus.NOT_SHARED,
            AgeSignalsStatus.VERIFICATION_REQUIRED,
            AgeSignalsStatus.UNSPECIFIED,
            null,
            -> AudienceContext.Unavailable
            else -> AudienceContext.Unavailable
        }
    }

    private suspend fun requestAccess(activity: Activity): Int? =
        suspendCancellableCoroutine { continuation ->
            try {
                val request =
                    AgeSignalsAccessRequest
                        .builder()
                        .setActivity(activity)
                        .build()
                manager
                    .requestAgeSignalsAccess(request)
                    .addOnSuccessListener { result ->
                        continuation.resume(result.ageSignalsStatus())
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } catch (ex: SecurityException) {
                continuation.resumeWithException(ex)
            } catch (ex: Exception) {
                continuation.resumeWithException(ex)
            }
        }

    private suspend fun checkSignals(): AudienceContext =
        suspendCancellableCoroutine { continuation ->
            try {
                manager
                    .checkAgeSignals(AgeSignalsRequest.builder().build())
                    .addOnSuccessListener { result ->
                        Timber.d("ageSignalsResult: $result")
                        continuation.resume(result.toAudienceContext())
                    }.addOnFailureListener { exception ->
                        continuation.resumeWithException(exception)
                    }
            } catch (ex: SecurityException) {
                continuation.resumeWithException(ex)
            } catch (ex: Exception) {
                continuation.resumeWithException(ex)
            }
        }

    companion object {
        private const val RETRY_DELAY_MS = 1000L
    }
}

internal fun AgeSignalsResult.toAudienceContext(): AudienceContext =
    AudienceContext.Resolved(
        lowerAge = ageLower(),
        status = mapAudienceStatus(ageRangeSource(), significantChangeStatus()),
    )

internal fun mapAudienceStatus(
    ageRangeSource: Int?,
    significantChangeStatus: Int?,
): AudienceStatus {
    when (significantChangeStatus) {
        SignificantChangeStatus.PENDING -> return AudienceStatus.Pending
        SignificantChangeStatus.DECLINED -> return AudienceStatus.Denied
    }

    return when (ageRangeSource) {
        AgeRangeSource.TIER_A -> AudienceStatus.Declared
        AgeRangeSource.TIER_B -> AudienceStatus.Supervised
        AgeRangeSource.TIER_C,
        AgeRangeSource.TIER_D,
        -> AudienceStatus.Verified
        else -> AudienceStatus.Unknown
    }
}
