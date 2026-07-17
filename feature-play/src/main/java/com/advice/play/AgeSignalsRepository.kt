@file:Suppress("GrazieInspectionRunner", "GrazieInspectionRunner")

package com.advice.play

import com.advice.core.audience.AudienceContext
import com.advice.core.audience.AudienceStatus
import com.google.android.play.agesignals.AgeSignalsException
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsRequest
import com.google.android.play.agesignals.model.AgeSignalsErrorCode
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

    suspend fun get(maxRetries: Int = 3): AudienceContext {
        var lastException: Exception? = null
        repeat(maxRetries) { attempt ->
            try {
                return fetchAgeSignals()
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

        // If we reach here, all retries failed or we encountered a non-retryable error
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
            AgeSignalsErrorCode.CLIENT_TRANSIENT_ERROR -> true
            else -> false
        }
    }

    private suspend fun fetchAgeSignals(): AudienceContext = suspendCancellableCoroutine { continuation ->
        try {
            manager.checkAgeSignals(AgeSignalsRequest.builder().build())
                .addOnSuccessListener { result ->
                    Timber.d("ageSignalsResult: $result")
                    continuation.resume(
                        AudienceContext.Resolved(
                            lowerAge = result.ageLower(),
                            status = AudienceStatus.getByValue(result.userStatus()),
                        )
                    )
                }
                .addOnFailureListener { exception ->
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
