package com.advice.reminder

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.core.utils.Time
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReminderManager(
    private val workManager: WorkManager
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
        workManager.cancelAllWorkByTag(reminder)
        val feedback = getTag(KEY_FEEDBACK, content, session)
        workManager.cancelAllWorkByTag(feedback)
    }

    private fun setSessionReminder(content: Content, session: Session) {
        val start = session.start
        val now = Time.now()

        val delay = start.toEpochMilli() - now.time - TWENTY_MINUTES_BEFORE

        if (delay < 0) {
            Timber.e("ReminderManager: Delay is negative: $delay - ignoring reminder")
            return
        }

        val data = workDataOf(
            ReminderWorker.INPUT_ID to content.id,
            ReminderWorker.INPUT_SESSION_ID to session.id,
            ReminderWorker.INPUT_CONFERENCE to content.conference,
            ReminderWorker.INPUT_ACTION to ReminderWorker.ACTION_REMINDER,
        )

        val tag = getTag(KEY_REMINDER, content, session)
        val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag(tag)
            .build()

        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, notify)
    }

    private fun setFeedbackReminder(
        content: Content,
        session: Session
    ) {
        if (content.feedback != null) {
            val enable = content.feedback?.enable ?: return
            val delay = enable.toEpochMilli() - Time.now().time - TWENTY_MINUTES_BEFORE
            if (delay < 0) {
                Timber.e("ReminderManager: Feedback delay is negative: $delay.")
                return
            }

            val data = workDataOf(
                ReminderWorker.INPUT_ID to content.id,
                ReminderWorker.INPUT_SESSION_ID to session.id,
                ReminderWorker.INPUT_CONFERENCE to content.conference,
                ReminderWorker.INPUT_ACTION to ReminderWorker.ACTION_FEEDBACK,
            )

            val tag = getTag(KEY_FEEDBACK, content, session)
            val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .build()

            workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, notify)
        }
    }

    private fun getTag(key: String, content: Content, session: Session): String {
        return "$key/${content.conference}/${content.id}:${session.id}"
    }
}
