package com.advice.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.advice.core.utils.NotificationHelper
import timber.log.Timber

class ReminderWorker(
    context: Context,
    params: WorkerParameters,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val conference = inputData.getString(INPUT_CONFERENCE)
        if (conference == null) {
            Timber.e("Could not fetch the current conference.")
            return Result.failure()
        }

        val id = inputData.getLong(INPUT_ID, -1)
        if (id == -1L) {
            Timber.e("Could not get the target id from the inputData.")
            return Result.failure()
        }

        val event = TODO()
//        if (event == null) {
//            Timber.e("Could not find the target event.")
//            return Result.failure()
//        }
//
//        // Event has already happened, skip this notification
//        if(event.hasStarted || event.hasFinished) {
//            return Result.success()
//        }

        notificationHelper.notifyStartingSoon(event)

        return Result.success()
    }

    companion object {
        const val INPUT_CONFERENCE = "INPUT_CONFERENCE"
        const val INPUT_ID = "INPUT_ID"
    }
}
