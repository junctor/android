package com.advice.reminder.di

import com.advice.core.utils.NotificationHelper
import com.advice.reminder.ReminderManager
import com.advice.reminder.ReminderWorker
import org.koin.androidx.workmanager.dsl.worker
import org.koin.dsl.module

val reminderModule =
    module {
        single { NotificationHelper(get()) }
        single { ReminderManager(get(), get()) }
        worker { params ->
            ReminderWorker(
                context = params.get(),
                params = params.get(),
                eventsDataSource = get(),
                notificationHelper = get(),
            )
        }
    }
