package com.advice.reminder

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.advice.core.local.Content
import com.advice.core.local.ReminderMinutes
import com.advice.core.local.Session
import com.advice.data.storage.UserPreferencesStore
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReminderManager(
    private val workManager: WorkManager,
    private val storage: UserPreferencesStore,
) {
    companion object {
        private const val KEY_REMINDER = "reminder"
        private const val KEY_FEEDBACK = "feedback"
        private const val MILLIS_PER_MINUTE = 60_000L
    }

    fun setReminders(
        content: Content,
        session: Session,
    ) {
        setSessionReminder(content, session)
        setFeedbackReminder(content, session)
    }

    fun updateReminders(
        content: Content,
        session: Session,
    ) {
        removeReminders(content, session)
        setSessionReminder(content, session)
        setFeedbackReminder(content, session)
    }

    fun removeReminders(
        content: Content,
        session: Session,
    ) {
        val reminder = getTag(KEY_REMINDER, content, session)
        workManager.cancelAllWorkByTag(reminder)
        val feedback = getTag(KEY_FEEDBACK, content, session)
        workManager.cancelAllWorkByTag(feedback)
    }

    private fun setSessionReminder(
        content: Content,
        session: Session,
    ) {
        val minutes = storage.eventReminderMinutes
        if (ReminderMinutes.isDisabled(minutes)) {
            return
        }

        val start = session.start
        val now = System.currentTimeMillis()
        val delay = start.toEpochMilli() - now - minutes * MILLIS_PER_MINUTE

        if (delay < 0) {
            Timber.e("ReminderManager: Delay is negative: $delay - ignoring reminder")
            return
        }

        val data =
            workDataOf(
                ReminderWorker.INPUT_ID to content.id,
                ReminderWorker.INPUT_SESSION_ID to session.id,
                ReminderWorker.INPUT_CONFERENCE to content.conference,
                ReminderWorker.INPUT_ACTION to ReminderWorker.ACTION_REMINDER,
            )

        val tag = getTag(KEY_REMINDER, content, session)
        val notify =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .build()

        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, notify)
    }

    private fun setFeedbackReminder(
        content: Content,
        session: Session,
    ) {
        if (content.feedback == null) {
            return
        }

        val minutes = storage.feedbackReminderMinutes
        if (ReminderMinutes.isDisabled(minutes)) {
            return
        }

        val enable = content.feedback?.enable ?: return
        val delay = enable.toEpochMilli() - System.currentTimeMillis() - minutes * MILLIS_PER_MINUTE
        if (delay < 0) {
            Timber.e("ReminderManager: Feedback delay is negative: $delay.")
            return
        }

        val data =
            workDataOf(
                ReminderWorker.INPUT_ID to content.id,
                ReminderWorker.INPUT_SESSION_ID to session.id,
                ReminderWorker.INPUT_CONFERENCE to content.conference,
                ReminderWorker.INPUT_ACTION to ReminderWorker.ACTION_FEEDBACK,
            )

        val tag = getTag(KEY_FEEDBACK, content, session)
        val notify =
            OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInputData(data)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(tag)
                .build()

        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.REPLACE, notify)
    }

    private fun getTag(
        key: String,
        content: Content,
        session: Session,
    ): String = "$key/${content.conference}/${content.id}:${session.id}"
}
