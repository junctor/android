package com.advice.reminder

import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.advice.core.local.Event
import com.advice.core.utils.Time
import java.util.concurrent.TimeUnit

class ReminderManager(
    private val workManager: WorkManager
) {

    companion object {
        private const val TWENTY_MINUTES_BEFORE = 1000 * 20 * 60
    }

    fun setReminder(event: Event) {
        val start = event.session.start
        val now = Time.now()

        val delay = start.toEpochMilli() - now.time - TWENTY_MINUTES_BEFORE

        if (delay < 0) {
            return
        }

        val data = workDataOf(
            ReminderWorker.INPUT_ID to event.id,
            ReminderWorker.INPUT_CONFERENCE to event.conference,
        )

        val notify = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInputData(data)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("reminder/${event.conference}/${event.id}")
            .build()

        workManager.enqueueUniqueWork(event.title, ExistingWorkPolicy.REPLACE, notify)
    }

    fun removeReminder(event: Event) {
        workManager.cancelAllWorkByTag("reminder/${event.conference}/${event.id}")
    }
}
