package com.advice.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.core.utils.Time
import timber.log.Timber

class ReminderManager(
    private val context: Context,
    private val alarmManager: AlarmManager,
) {

    companion object {
        private const val TWENTY_MINUTES_BEFORE = 1000 * 20 * 60
        private const val KEY_REMINDER = "reminder"
        private const val KEY_FEEDBACK = "feedback"
    }

    fun setReminders(content: Content, session: Session) {
        setSessionReminder(content, session)
        setFeedbackReminder(content, session)
    }

    fun updateReminders(content: Content, session: Session) {
        removeReminders(content, session)
        setSessionReminder(content, session)
        setFeedbackReminder(content, session)
    }

    fun removeReminders(content: Content, session: Session) {
        val reminder = getTag(KEY_REMINDER, content, session)
        // todo: workManager.cancelAllWorkByTag(reminder)
        val feedback = getTag(KEY_FEEDBACK, content, session)
        // todo: workManager.cancelAllWorkByTag(feedback)
    }

    private fun setSessionReminder(content: Content, session: Session) {
        val start = session.start
        val now = Time.now()

        val delay: Long =
            now.time + (30 * 1000) // start.toEpochMilli() - now.time - TWENTY_MINUTES_BEFORE

        if (delay < 0) {
            Timber.e("ReminderManager: Delay is negative: $delay - ignoring reminder")
            return
        }

        val pendingIntent = getWorkData(content, session, ReminderWorker.ACTION_REMINDER)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, delay, pendingIntent)
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent)
        }
    }

    private fun setFeedbackReminder(
        content: Content, session: Session
    ) {
        if (content.feedback != null) {
            val enable = content.feedback?.enable ?: return
            // todo: update the delay to a epoch time.
            val delay = enable.toEpochMilli() - Time.now().time - TWENTY_MINUTES_BEFORE

            if (delay < 0) {
                Timber.e("ReminderManager: Feedback delay is negative: $delay.")
                return
            }

            val pendingIntent = getWorkData(content, session, ReminderWorker.ACTION_FEEDBACK)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, delay, pendingIntent)
        }
    }

    private fun getTag(key: String, content: Content, session: Session): String {
        return "$key/${content.conference}/${content.id}:${session.id}"
    }

    private fun getWorkData(
        content: Content,
        session: Session,
        action: String,
    ): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java)

        intent.putExtra(ReminderWorker.INPUT_CONFERENCE, content.conference)
        intent.putExtra(ReminderWorker.INPUT_CONTENT_ID, content.id)
        intent.putExtra(ReminderWorker.INPUT_SESSION_ID, session.id)
        intent.putExtra(ReminderWorker.INPUT_TITLE, content.title)
        intent.putExtra(ReminderWorker.INPUT_START, session.start.toEpochMilli())
        intent.putExtra(ReminderWorker.INPUT_END, session.end.toEpochMilli())
        intent.putExtra(ReminderWorker.INPUT_LOCATION, session.location.name)
        intent.putExtra(ReminderWorker.INPUT_ACTION, action)

        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
