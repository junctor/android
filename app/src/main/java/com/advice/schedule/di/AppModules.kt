package com.advice.schedule.di

import com.advice.documents.di.documentsModule
import com.advice.feedback.di.feedbackModule
import com.advice.locations.di.locationsModule
import com.advice.organizations.di.organizationsModule
import com.advice.products.di.productsModule
import com.advice.reminder.di.reminderModule
import com.advice.wifi.di.wifiModule
import com.shortstack.hackertracker.BuildConfig
import org.koin.core.module.Module

fun appModules(): List<Module> =
    listOf(
        shellModule,
        firebaseDataModule,
        reminderModule,
        scheduleModule,
        settingsModule,
        organizationsModule,
        wifiModule,
        feedbackModule("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
        locationsModule,
        productsModule(BuildConfig.VERSION_CODE),
        documentsModule,
        playModule,
    )
