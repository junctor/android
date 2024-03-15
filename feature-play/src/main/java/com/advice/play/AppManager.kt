package com.advice.play

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlin.coroutines.suspendCoroutine

class AppManager(context: Context) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)

    suspend fun isUpdateAvailable(): Boolean {
        return suspendCoroutine {
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                it.resumeWith(Result.success(appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE))
            }
        }
    }

    fun checkForUpdate(activity: Activity, requestCode: Int) {
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
}