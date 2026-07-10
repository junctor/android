package com.advice.play

import android.app.Activity
import android.content.Context
import com.google.android.play.agesignals.AgeSignalsManagerFactory
import com.google.android.play.agesignals.AgeSignalsRequest
import com.google.android.play.agesignals.model.AgeSignalsVerificationStatus
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.suspendCoroutine

class AppManager(context: Context) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    private val ageSignalsManager = AgeSignalsManagerFactory.create(context)

    suspend fun isUpdateAvailable(): Boolean {
        if (BuildConfig.DEBUG) {
            return false
        }

        return suspendCoroutine {
            try {
                appUpdateManager.appUpdateInfo
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            it.resumeWith(Result.success(task.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE))
                        } else {
                            it.resumeWith(Result.success(false))
                        }
                    }
            } catch (ex: SecurityException) {
                it.resumeWith(Result.success(false))
            } catch (ex: Exception) {
                it.resumeWith(Result.success(false))
            }
        }
    }

    fun checkForUpdate(activity: Activity, requestCode: Int) {
        if (BuildConfig.DEBUG) {
            return
        }

        try {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.FLEXIBLE,
                        activity,
                        requestCode
                    )
                }
            }
        } catch (ex: Exception) {
            // ignore
        }
    }

    suspend fun isAllowedMatureContent(): Boolean {
        if (BuildConfig.DEBUG) {
            return true
        }

        return suspendCoroutine { routine ->
            try {
                // Request an age signals check
                ageSignalsManager
                    .checkAgeSignals(AgeSignalsRequest.builder().build())
                    .addOnSuccessListener { ageSignalsResult ->
                        val verified = ageSignalsResult.userStatus() == AgeSignalsVerificationStatus.VERIFIED
                        val minAge = ageSignalsResult.ageLower() ?: 0
                        // Actual 'mature content' threshold likely varies by jurisdiction
                        routine.resumeWith(Result.success(verified && minAge >= 18))
                    }
                    .addOnFailureListener {
                        routine.resumeWith(Result.failure(it))
                    }
            } catch (ex: SecurityException) {
                routine.resumeWith(Result.success(false))
            } catch (ex: Exception) {
                routine.resumeWith(Result.failure(ex))
            }
        }
    }

    suspend fun lowerAge(): Int {
        if (BuildConfig.DEBUG) {
            return 18
        }

        return suspendCoroutine { routine ->
            try {
                // Request an age signals check
                ageSignalsManager
                    .checkAgeSignals(AgeSignalsRequest.builder().build())
                    .addOnSuccessListener { ageSignalsResult ->

                        if (ageSignalsResult.userStatus() == AgeSignalsVerificationStatus.VERIFIED) {
                            // Get the age lower bound
                            routine.resumeWith(Result.success(ageSignalsResult.ageLower() ?: 0))
                        } else {
                            // Do something else if the user is DECLARED, SUPERVISED, etc.
                            routine.resumeWith(Result.success(0))
                        }
                    }
                    .addOnFailureListener {
                        routine.resumeWith(Result.failure(it))
                    }
            } catch (ex: SecurityException) {
                routine.resumeWith(Result.success(0))
            } catch (ex: Exception) {
                routine.resumeWith(Result.failure(ex))
            }
        }
    }
}
