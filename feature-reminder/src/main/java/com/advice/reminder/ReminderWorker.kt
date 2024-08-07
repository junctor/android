package com.advice.reminder

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.advice.core.utils.NotificationHelper
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ReminderWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

    private val notificationHelper by inject<NotificationHelper>()

    override suspend fun doWork(): Result {
        val action = inputData.getString(INPUT_ACTION) ?: ACTION_REMINDER

        Timber.d("ReminderWorker.doWork($action)")
        val conference = inputData.getString(INPUT_CONFERENCE)
        if (conference == null) {
            Timber.e("Could not fetch the current conference.")
            return Result.failure()
        }

        val id = inputData.getLong(INPUT_CONTENT_ID, -1)
        if (id == -1L) {
            Timber.e("Could not get the target id from the inputData.")
            return Result.failure()
        }

        val session = inputData.getLong(INPUT_SESSION_ID, -1)
        if (session == -1L) {
            Timber.e("Could not get the target session from the inputData.")
            return Result.failure()
        }

        val title = inputData.getString(INPUT_TITLE)
        if (title == null) {
            Timber.e("Could not get the title from the inputData.")
            return Result.failure()
        }

        val location = inputData.getString(INPUT_LOCATION)
        if (location == null) {
            Timber.e("Could not get the location from the inputData.")
            return Result.failure()
        }

        when (action) {
            // Remind the user the event is starting soon
            ACTION_REMINDER -> {
                notificationHelper.notifyStartingSoon(conference, id, session, title, location)
            }

            // Remind the user to provide feedback
            ACTION_FEEDBACK -> {
                notificationHelper.notifyFeedbackAvailable(conference, id, session, title)
            }
        }

        return Result.success()
    }

    companion object {
        const val INPUT_CONFERENCE = "INPUT_CONFERENCE"
        const val INPUT_CONTENT_ID = "INPUT_ID"
        const val INPUT_SESSION_ID = "INPUT_SESSION_ID"
        const val INPUT_ACTION = "INPUT_ACTION"
        const val INPUT_TITLE = "INPUT_TITLE"
        const val INPUT_START = "INPUT_START"
        const val INPUT_END = "INPUT_END"
        const val INPUT_LOCATION = "INPUT_LOCATION"

        const val ACTION_REMINDER = "ACTION_REMINDER"
        const val ACTION_FEEDBACK = "ACTION_FEEDBACK"
    }
}
