package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.schedule.PreferenceViewModel
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.ReminderManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.database.datasource.ArticleDataSource
import com.advice.schedule.database.datasource.BookmarkedElementDataSource
import com.advice.schedule.database.datasource.ConferencesDataSource
import com.advice.schedule.database.datasource.EventsDataSource
import com.advice.schedule.database.datasource.FAQDataSource
import com.advice.schedule.database.datasource.FirebaseBookmarkedTagsDataSource
import com.advice.schedule.database.datasource.FirebaseTagsDataSourceImpl
import com.advice.schedule.database.datasource.InMemoryBookmarkedDataSourceImpl
import com.advice.schedule.database.datasource.LocationsDataSource
import com.advice.schedule.database.datasource.MapsDataSource
import com.advice.schedule.database.datasource.SpeakersDataSource
import com.advice.schedule.database.datasource.TagsDataSource
import com.advice.schedule.database.datasource.VendorsDataSource
import com.advice.schedule.database.repository.FAQRepository
import com.advice.schedule.database.repository.FiltersRepository
import com.advice.schedule.database.repository.HomeRepository
import com.advice.schedule.database.repository.ScheduleRepository
import com.advice.schedule.database.repository.SpeakersRepository
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.information.faq.FAQViewModel
import com.advice.schedule.ui.information.info.ConferenceViewModel
import com.advice.schedule.ui.information.locations.LocationsViewModel
import com.advice.schedule.ui.information.speakers.SpeakersViewModel
import com.advice.schedule.ui.information.vendors.VendorsViewModel
import com.advice.schedule.ui.schedule.FiltersViewModel
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.ui.settings.SettingsViewModel
import com.advice.schedule.utilities.Analytics
import com.advice.schedule.utilities.NotificationHelper
import com.advice.schedule.utilities.Storage
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { Storage(get(), get()) }
    single { NotificationHelper(get()) }
    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }
    single { FirebaseJobDispatcher(GooglePlayDriver(get())) }
    single { DatabaseManager(get()/* get()*/) }

    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }

    single { Analytics(get()) }
    single { WorkManager.getInstance() }
    single { ReminderManager(get(), get()) }

    // auth
    single { UserSession(get(), get()) }

    // repo
    single { ScheduleRepository(get(), get()) }
    single { HomeRepository(get(), get(), get()) }
    single { SpeakersRepository(get(), get()) }
    single { FiltersRepository(get(), get()) }
    single { FAQRepository(get()) }

    // data source
    single { ConferencesDataSource(get()) }
    single { EventsDataSource(get(), get(), get(), get()) }

//    single<BookmarkedElementDataSource> { BookmarksDataSourceImpl(get(), get()) }
    single<BookmarkedElementDataSource> { InMemoryBookmarkedDataSourceImpl() }

    single<TagsDataSource> { FirebaseTagsDataSourceImpl(get(), get(), get()) }
    single { FirebaseBookmarkedTagsDataSource(get(), get()) }


    single { ArticleDataSource(get(), get()) }
    single { FAQDataSource(get(), get()) }
    single { LocationsDataSource(get(), get()) }
    single { MapsDataSource(get(), get()) }
    single { VendorsDataSource(get(), get()) }
    single { SpeakersDataSource(get(), get()) }


    viewModel { HomeViewModel() }
    viewModel { HackerTrackerViewModel() }
    viewModel { PreferenceViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { SpeakersViewModel() }
    viewModel { LocationsViewModel() }
    viewModel { VendorsViewModel() }
    viewModel { FAQViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { FiltersViewModel() }
    viewModel { ConferenceViewModel() }

}