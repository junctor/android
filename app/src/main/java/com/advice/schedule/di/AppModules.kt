package com.advice.schedule.di

import com.advice.analytics.di.analyticsModule
import com.advice.documents.di.documentsModule
import com.advice.faq.di.faqModule
import com.advice.feedback.di.feedbackModule
import com.advice.firebase.di.firebaseDataModule
import com.advice.locations.di.locationsModule
import com.advice.maps.di.mapsModule
import com.advice.menu.di.menuModule
import com.advice.merch.di.merchModule
import com.advice.news.di.newsModule
import com.advice.organizations.di.organizationsModule
import com.advice.play.di.playModule
import com.advice.reminder.di.reminderModule
import com.advice.retrofit.di.retrofitModule
import com.advice.search.di.searchModule
import com.advice.settings.di.settingsModule
import com.advice.speakers.di.speakersModule
import com.advice.wifi.di.wifiModule
import com.shortstack.hackertracker.BuildConfig
import org.koin.core.module.Module

fun appModules(): List<Module> =
    listOf(
        shellModule,
        analyticsModule(BuildConfig.VERSION_CODE),
        firebaseDataModule,
        retrofitModule,
        reminderModule,
        scheduleModule,
        settingsModule("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
        mapsModule,
        newsModule,
        faqModule,
        speakersModule,
        searchModule,
        menuModule,
        organizationsModule,
        wifiModule,
        feedbackModule("${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"),
        locationsModule,
        merchModule(BuildConfig.VERSION_CODE),
        documentsModule,
        playModule,
    )
