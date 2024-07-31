package com.advice.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.advice.core.utils.NotificationHelper
import com.advice.data.sources.ContentDataSource
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val eventsDataSource by inject<ContentDataSource>()
    private val notificationHelper by inject<NotificationHelper>()

    override suspend fun doWork(): Result {
        Timber.d("ReminderWorker.doWork()")
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

        val session = inputData.getLong(INPUT_SESSION_ID, -1)
        if (session == -1L) {
            Timber.e("Could not get the target session from the inputData.")
            return Result.failure()
        }

        val event = eventsDataSource.getEvent(conference, id, session)
        if (event == null) {
            Timber.e("Could not find the target event.")
            return Result.failure()
        }

        // Event has already happened, skip this notification
        if (event.hasStarted || event.hasFinished) {
            Timber.e("Event has already finished.")
            return Result.success()
        }

        notificationHelper.notifyStartingSoon(event)

        return Result.success()
    }

    companion object {
        const val INPUT_CONFERENCE = "INPUT_CONFERENCE"
        const val INPUT_ID = "INPUT_ID"
        const val INPUT_SESSION_ID = "INPUT_SESSION_ID"
    }
}
