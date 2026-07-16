package com.advice.play

import android.app.Activity
import android.content.Context
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.suspendCancellableCoroutine
class AppManager(context: Context) {

    private val appUpdateManager = AppUpdateManagerFactory.create(context)
    suspend fun isUpdateAvailable(): Boolean {
        if (BuildConfig.DEBUG) {
            return false
        }

        return suspendCancellableCoroutine {
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
                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        activity,
                        AppUpdateOptions.newBuilder(AppUpdateType.FLEXIBLE).build(),
                        requestCode
                    )
                }
            }
        } catch (ex: Exception) {
            // ignore
        }
    }
}
