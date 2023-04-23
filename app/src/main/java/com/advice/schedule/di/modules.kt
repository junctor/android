package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.core.utils.Storage
import com.advice.data.UserSession
import com.advice.data.datasource.*
import com.advice.firebase.FirebaseUserSession
import com.advice.firebase.datasource.*
import com.advice.locations.data.LocationRepository
import com.advice.merch.MerchViewModel
import com.advice.merch.data.LocalMerchDataSource
import com.advice.merch.data.MerchRepository
import com.advice.schedule.repository.*
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.information.InformationViewModel
import com.advice.schedule.ui.information.faq.FAQViewModel
import com.advice.schedule.ui.information.info.ConferenceViewModel
import com.advice.schedule.ui.information.speakers.SpeakerViewModel
import com.advice.schedule.ui.information.speakers.SpeakersViewModel
import com.advice.schedule.ui.information.vendors.VendorsViewModel
import com.advice.schedule.ui.maps.MapViewModel
import com.advice.schedule.ui.schedule.FiltersViewModel
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.ui.settings.SettingsViewModel
import com.advice.schedule.ui.tablet.TabletViewModel
import com.advice.schedule.utilities.Analytics
import com.firebase.jobdispatcher.FirebaseJobDispatcher
import com.firebase.jobdispatcher.GooglePlayDriver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { Storage(get(), get()) }

    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }
    single { FirebaseJobDispatcher(GooglePlayDriver(get())) }

    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }

    single { Analytics(get()) }
    single { WorkManager.getInstance() }

    // todo: Reminder
//    single { com.advice.reminder.NotificationHelper(get()) }
//    single { com.advice.reminder.ReminderManager(get()) }

    // auth


    // repo
    single { ScheduleRepository(get(), get()) }
    single { HomeRepository(get(), get(), get()) }
    single { SpeakersRepository(get()) }
    single { SpeakerRepository(get()) }
    single { FiltersRepository(get(), get()) }
    single { FAQRepository(get()) }
    single { SettingsRepository(get(), get(), "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") }
    single { MapRepository(get()) }
    single { LocationRepository(get()) }
    single { VendorsRepository(get()) }
    single { InformationRepository(get()) }
    single { MerchRepository(androidContext(), get()) }


//    single<BookmarkedElementDataSource> { BookmarksDataSourceImpl(get(), get()) }
    single<BookmarkedElementDataSource> { InMemoryBookmarkedDataSourceImpl() }

    single<UserSession> { FirebaseUserSession(get(), get(), get()) }
    single<ArticleDataSource> { FirebaseArticleDataSource(get(), get()) }
    single<ConferencesDataSource> { FirebaseConferencesDataSource(get()) }
    single<EventsDataSource> { FirebaseEventsDataSource(get(), get(), get(), get()) }
    single<TagsDataSource> { FirebaseTagsDataSource(get(), get(), get()) }
    single<FAQDataSource> { FirebaseFAQDataSource(get(), get()) }
    single<LocationsDataSource> { FirebaseLocationsDataSource(get(), get()) }
    single<MapsDataSource> { FirebaseMapsDataSource(get(), androidContext().applicationContext.getExternalFilesDir(null), get(), ) }
    single<VendorsDataSource> { FirebaseVendorsDataSource(get(), get()) }
    single<SpeakersDataSource> { FirebaseSpeakersDataSource(get(), get()) }
    single<MerchDataSource> { LocalMerchDataSource() }

    viewModel { HomeViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { SpeakerViewModel() }
    viewModel { SpeakersViewModel() }
    viewModel { MapViewModel() }
    viewModel { InformationViewModel() }
    viewModel { com.advice.locations.LocationsViewModel() }
    viewModel { VendorsViewModel() }
    viewModel { FAQViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { FiltersViewModel() }
    viewModel { ConferenceViewModel() }

    viewModel { TabletViewModel() }

    viewModel { MerchViewModel() }

}