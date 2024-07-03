package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.utils.NotificationHelper
import com.advice.core.utils.Storage
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import com.advice.data.SharedPreferencesBookmarkDataSource
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.FAQDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.MapsDataSource
import com.advice.data.sources.MenuDataSource
import com.advice.data.sources.NewsDataSource
import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.ProductsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.data.sources.VendorsDataSource
import com.advice.data.sources.VillagesDataSource
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.firebase.data.sources.FirebaseConferencesDataSource
import com.advice.firebase.data.sources.FirebaseContentDataSource
import com.advice.firebase.data.sources.FirebaseDocumentsDataSource
import com.advice.firebase.data.sources.FirebaseFAQDataSource
import com.advice.firebase.data.sources.FirebaseLocationsDataSource
import com.advice.firebase.data.sources.FirebaseMapsDataSource
import com.advice.firebase.data.sources.FirebaseMenuDataSource
import com.advice.firebase.data.sources.FirebaseNewsDataSource
import com.advice.firebase.data.sources.FirebaseOrganizationDataSource
import com.advice.firebase.data.sources.FirebaseProductsDataSource
import com.advice.firebase.data.sources.FirebaseSpeakersDataSource
import com.advice.firebase.data.sources.FirebaseTagsDataSource
import com.advice.firebase.data.sources.FirebaseVendorsDataSource
import com.advice.firebase.data.sources.FirebaseVillagesDataSource
import com.advice.firebase.session.FirebaseUserSession
import com.advice.locations.data.repositories.LocationRepository
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import com.advice.play.AppManager
import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.viewmodel.ProductCart
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.reminder.ReminderManager
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.EventRepository
import com.advice.schedule.data.repositories.FAQRepository
import com.advice.schedule.data.repositories.FiltersRepository
import com.advice.schedule.data.repositories.HomeRepository
import com.advice.schedule.data.repositories.InformationRepository
import com.advice.schedule.data.repositories.MapRepository
import com.advice.schedule.data.repositories.MenuRepository
import com.advice.schedule.data.repositories.OrganizationsRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.SearchRepository
import com.advice.schedule.data.repositories.SettingsRepository
import com.advice.schedule.data.repositories.SpeakerRepository
import com.advice.schedule.data.repositories.SpeakersRepository
import com.advice.schedule.data.repositories.TagsRepository
import com.advice.schedule.presentation.viewmodel.ConferenceViewModel
import com.advice.schedule.presentation.viewmodel.FAQViewModel
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.InformationViewModel
import com.advice.schedule.presentation.viewmodel.MapsViewModel
import com.advice.schedule.presentation.viewmodel.OrganizationsViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.presentation.viewmodel.SearchViewModel
import com.advice.schedule.presentation.viewmodel.SettingsViewModel
import com.advice.schedule.presentation.viewmodel.SpeakerViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import com.advice.schedule.navigation.NavigationManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {

    single { Storage(get(), get()) }

    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }

    single { FirebaseCrashlytics.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseAnalytics.getInstance(androidContext()) }

    // work manager
    single { WorkManager.getInstance(androidContext()) }

    // update manager
    single { AppManager(androidContext()) }

    single { AnalyticsProvider(get()) }

    // reminder
    single { NotificationHelper(get()) }
    single { ReminderManager(get()) }

    // navigation
    single { NavigationManager(get()) }

    // repo
    single { ScheduleRepository(get(), get(), get()) }
    single { HomeRepository(get(), get(), get(), get(), get()) }
    single { SpeakersRepository(get()) }
    single { ContentRepository(get()) }
    single { EventRepository(get()) }
    single { SpeakerRepository(get(), get()) }
    single { FiltersRepository(get(), get(named("tags"))) }
    single { FAQRepository(get()) }
    single {
        SettingsRepository(
            get(),
            get(),
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        )
    }
    single { MapRepository(get()) }
    single { LocationRepository(get()) }
    single { OrganizationsRepository(get()) }
    single { InformationRepository(get(), get(), get(), get()) }
    single { ProductsRepository(get(), get()) }
    single { DocumentsRepository(get()) }
    single { TagsRepository(get()) }
    single { SearchRepository(get(), get(), get(), get(), get(), get()) }
    single { MenuRepository(get()) }

//    single<BookmarkedElementDataSource> { BookmarksDataSourceImpl(get(), get()) }
    single<BookmarkedElementDataSource>(named("tags")) { InMemoryBookmarkedDataSourceImpl() }
    single<BookmarkedElementDataSource>(named("events")) {
        SharedPreferencesBookmarkDataSource(
            androidContext(),
        )
    }

    single<UserSession> { FirebaseUserSession(get(), get(), get(), get()) }
    single<NewsDataSource> { FirebaseNewsDataSource(get(), get(), get()) }
    single<ConferencesDataSource> { FirebaseConferencesDataSource(get(), get()) }
    single<ContentDataSource> {
        FirebaseContentDataSource(
            get(),
            get(),
            get(),
            get(),
            get(),
            get<LocationsDataSource>(),
            get(named("events")),
        )
    }
    single<TagsDataSource> { FirebaseTagsDataSource(get(), get(), get(), get(named("tags"))) }
    single<FAQDataSource> { FirebaseFAQDataSource(get(), get(), get()) }
    single<LocationsDataSource> { FirebaseLocationsDataSource(get(), get(), get()) }
    single<MapsDataSource> {
        FirebaseMapsDataSource(
            get(),
            androidContext().applicationContext.getExternalFilesDir(null),
            get(),
        )
    }

    single<SpeakersDataSource> { FirebaseSpeakersDataSource(get(), get(), get()) }
    single<ProductsDataSource> { FirebaseProductsDataSource(get(), get(), get()) }

    // Organizations
    single<OrganizationsDataSource> { FirebaseOrganizationDataSource(get(), get(), get()) }
    single<VendorsDataSource> { FirebaseVendorsDataSource(get(), get()) }
    single<VillagesDataSource> { FirebaseVillagesDataSource(get(), get()) }

    // Documents
    single<DocumentsDataSource> { FirebaseDocumentsDataSource(get(), get(), get()) }

    single<MenuDataSource> { FirebaseMenuDataSource(get(), get(), get()) }

    // Products
    single<ProductCart> { ProductCart() }

    viewModel { HomeViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { SpeakerViewModel() }
    viewModel { SpeakersViewModel() }
    viewModel { MapsViewModel() }
    viewModel { InformationViewModel() }
    viewModel { LocationsViewModel() }
    viewModel { OrganizationsViewModel() }
    viewModel { FAQViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { FiltersViewModel() }
    viewModel { ConferenceViewModel() }
    viewModel { SearchViewModel() }

    viewModel { ProductsViewModel() }
}
