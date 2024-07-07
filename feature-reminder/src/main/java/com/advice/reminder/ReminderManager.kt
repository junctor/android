package com.advice.reminder

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.Session
import com.advice.core.utils.Time
import java.util.concurrent.TimeUnit

class ReminderManager(
    private val workManager: WorkManager
) {

    companion object {
        private const val TWENTY_MINUTES_BEFORE = 1000 * 20 * 60
    }

    fun setReminder(content: Content, session: Session) {
        setReminder(session, session.id, content.conference, content.title)
    }

    private fun setReminder(
        session: Session,
        id: Long,
        conference: String,
        title: String
    ) {
        val start = session.start
        val now = Time.now()

        val delay = start.toEpochMilli() - now.time - TWENTY_MINUTES_BEFORE

        if (delay < 0) {
            return
        }

        val data = workDataOf(
            ReminderWorker.INPUT_ID to id,
            ReminderWorker.INPUT_CONFERENCE to conference,
        )

        val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("reminder/$conference/$id")
            .build()


        workManager.enqueueUniqueWork(title, ExistingWorkPolicy.REPLACE, notify)
    }

    fun removeReminder(content: Content, session: Session) {
        workManager.cancelAllWorkByTag("reminder/${content.conference}/${session.id}")
    }
}
