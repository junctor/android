package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.core.utils.NotificationHelper
import com.advice.core.utils.Storage
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import com.advice.data.session.UserSession
import com.advice.data.sources.ArticleDataSource
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.EventsDataSource
import com.advice.data.sources.FAQDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.MapsDataSource
import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.ProductsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.firebase.session.FirebaseUserSession
import com.advice.firebase.data.sources.FirebaseArticleDataSource
import com.advice.firebase.data.sources.FirebaseConferencesDataSource
import com.advice.firebase.data.sources.FirebaseDocumentsDataSource
import com.advice.firebase.data.sources.FirebaseEventsDataSource
import com.advice.firebase.data.sources.FirebaseFAQDataSource
import com.advice.firebase.data.sources.FirebaseLocationsDataSource
import com.advice.firebase.data.sources.FirebaseMapsDataSource
import com.advice.firebase.data.sources.FirebaseOrganizationDataSource
import com.advice.firebase.data.sources.FirebaseProductsDataSource
import com.advice.firebase.data.sources.FirebaseSpeakersDataSource
import com.advice.firebase.data.sources.FirebaseTagsDataSource
import com.advice.firebase.data.sources.FirebaseVendorsDataSource
import com.advice.firebase.data.sources.FirebaseVillagesDataSource
import com.advice.locations.data.repositories.LocationRepository
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.products.data.repositories.ProductsRepository
import com.advice.reminder.ReminderManager
import com.advice.schedule.repository.FAQRepository
import com.advice.schedule.repository.FiltersRepository
import com.advice.schedule.repository.HomeRepository
import com.advice.schedule.repository.InformationRepository
import com.advice.schedule.repository.MapRepository
import com.advice.schedule.repository.OrganizationsRepository
import com.advice.schedule.repository.ScheduleRepository
import com.advice.schedule.repository.SettingsRepository
import com.advice.schedule.repository.SpeakerRepository
import com.advice.schedule.repository.SpeakersRepository
import com.advice.schedule.ui.home.HomeViewModel
import com.advice.schedule.ui.information.InformationViewModel
import com.advice.schedule.ui.information.faq.FAQViewModel
import com.advice.schedule.ui.information.info.ConferenceViewModel
import com.advice.schedule.ui.information.speakers.SpeakerViewModel
import com.advice.schedule.ui.information.speakers.SpeakersViewModel
import com.advice.schedule.ui.information.vendors.OrganizationsViewModel
import com.advice.schedule.ui.maps.MapViewModel
import com.advice.schedule.ui.schedule.FiltersViewModel
import com.advice.schedule.ui.schedule.ScheduleViewModel
import com.advice.schedule.ui.settings.SettingsViewModel
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

    // work manager
    single { WorkManager.getInstance(androidContext()) }

    single { Analytics(get()) }

    // reminder
    single { NotificationHelper(get()) }
    single { ReminderManager(get()) }

    // repo
    single { ScheduleRepository(get(), get(), get()) }
    single { HomeRepository(get(), get(), get()) }
    single { SpeakersRepository(get()) }
    single { SpeakerRepository(get()) }
    single { FiltersRepository(get(), get()) }
    single { FAQRepository(get()) }
    single { SettingsRepository(get(), get(), "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})") }
    single { MapRepository(get()) }
    single { LocationRepository(get()) }
    single { OrganizationsRepository(get(), get()) }
    single { InformationRepository(get(), get(), get(), get()) }
    single { ProductsRepository(get()) }
    single { DocumentsRepository(get()) }


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

    single<SpeakersDataSource> { FirebaseSpeakersDataSource(get(), get()) }
    single<ProductsDataSource> { FirebaseProductsDataSource(get(), get()) }

    // Organizations
    single<OrganizationsDataSource> { FirebaseOrganizationDataSource(get(), get()) }
    single<VendorsDataSource> { FirebaseVendorsDataSource(get(), get()) }
    single<VillagesDataSource>{ FirebaseVillagesDataSource(get(), get()) }

    // Documents
    single<DocumentsDataSource> { FirebaseDocumentsDataSource(get(), get()) }

    viewModel { HomeViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { SpeakerViewModel() }
    viewModel { SpeakersViewModel() }
    viewModel { MapViewModel() }
    viewModel { InformationViewModel() }
    viewModel { LocationsViewModel() }
    viewModel { OrganizationsViewModel() }
    viewModel { FAQViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { FiltersViewModel() }
    viewModel { ConferenceViewModel() }

    viewModel { ProductsViewModel() }

}