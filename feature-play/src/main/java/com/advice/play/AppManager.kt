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
            appUpdateManager.appUpdateInfo
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        it.resumeWith(Result.success(task.result.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE))
                    } else {
                        it.resumeWith(Result.success(false))
                    }
                }
        }
    }

    fun checkForUpdate(activity: Activity, requestCode: Int) {
        if (BuildConfig.DEBUG) {
            return
        }

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
    }

    suspend fun isAllowedMatureContent(): Boolean {
        if (BuildConfig.DEBUG) {
            return true
        }


        return suspendCoroutine { routine ->
            // Request an age signals check
            ageSignalsManager
                .checkAgeSignals(AgeSignalsRequest.builder().build())
                .addOnSuccessListener { ageSignalsResult ->
                    routine.resumeWith(Result.success(ageSignalsResult.userStatus() == AgeSignalsVerificationStatus.VERIFIED))
                }
                .addOnFailureListener {
                    routine.resumeWith(Result.failure(it))
                }
        }
    }
}
