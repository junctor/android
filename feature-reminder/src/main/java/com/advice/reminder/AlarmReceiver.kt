package com.advice.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.advice.core.utils.NotificationHelper
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Timber.e("AlarmReceiver: onReceive($intent)")
        val notificationHelper = NotificationHelper(context)

        val action =
            intent.getStringExtra(ReminderWorker.INPUT_ACTION) ?: ReminderWorker.ACTION_REMINDER

        Timber.d("ReminderWorker.doWork($action)")
        val conference = intent.getStringExtra(ReminderWorker.INPUT_CONFERENCE)
        if (conference == null) {
            Timber.e("Could not fetch the current conference.")
            return
        }

        val id = intent.getLongExtra(ReminderWorker.INPUT_CONTENT_ID, -1)
        if (id == -1L) {
            Timber.e("Could not get the target id from the inputData.")
            return
        }

        val session = intent.getLongExtra(ReminderWorker.INPUT_SESSION_ID, -1)
        if (session == -1L) {
            Timber.e("Could not get the target session from the inputData.")
            return
        }

        val title = intent.getStringExtra(ReminderWorker.INPUT_TITLE)
        if (title == null) {
            Timber.e("Could not get the title from the inputData.")
            return
        }

        val location = intent.getStringExtra(ReminderWorker.INPUT_LOCATION)
        if (location == null) {
            Timber.e("Could not get the location from the inputData.")
            return
        }

        when (action) {
            // Remind the user the event is starting soon
            ReminderWorker.ACTION_REMINDER -> {
                notificationHelper.notifyStartingSoon(conference, id, session, title, location)
            }

            // Remind the user to provide feedback
            ReminderWorker.ACTION_FEEDBACK -> {
                notificationHelper.notifyFeedbackAvailable(conference, id, session, title)
            }
        }
    }
}
