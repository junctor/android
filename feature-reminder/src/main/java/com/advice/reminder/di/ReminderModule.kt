package com.advice.reminder.di

import com.advice.core.utils.NotificationHelper
import com.advice.reminder.ReminderManager
import org.koin.dsl.module

val reminderModule =
    module {
        single { NotificationHelper(get()) }
        single { ReminderManager(get(), get()) }
    }
