package com.advice.schedule.di

import android.content.Context.WIFI_SERVICE
import android.net.wifi.WifiManager
import androidx.work.WorkManager
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.audience.AudiencePolicy
import com.advice.core.audience.FailOpenAudiencePolicy
import com.advice.core.utils.NotificationHelper
import com.advice.core.utils.Storage
import com.advice.core.utils.ToastManager
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import com.advice.data.SharedPreferencesBookmarkDataSource
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.FAQDataSource
import com.advice.data.sources.FeedbackDataSource
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
import com.advice.data.sources.WiFiNetworksDataSource
import com.advice.documents.data.repositories.DocumentsRepository
import com.advice.feedback.network.FeedbackSubmissionRepository
import com.advice.firebase.data.sources.FirebaseConferencesDataSource
import com.advice.firebase.data.sources.FirebaseContentDataSource
import com.advice.firebase.data.sources.FirebaseDocumentsDataSource
import com.advice.firebase.data.sources.FirebaseFAQDataSource
import com.advice.firebase.data.sources.FirebaseFeedbackDataSource
import com.advice.firebase.data.sources.FirebaseLocationsDataSource
import com.advice.firebase.data.sources.FirebaseMenuDataSource
import com.advice.firebase.data.sources.FirebaseNewsDataSource
import com.advice.firebase.data.sources.FirebaseOrganizationDataSource
import com.advice.firebase.data.sources.FirebaseProductsDataSource
import com.advice.firebase.data.sources.FirebaseSpeakersDataSource
import com.advice.firebase.data.sources.FirebaseTagsDataSource
import com.advice.firebase.data.sources.FirebaseVendorsDataSource
import com.advice.firebase.data.sources.FirebaseVillagesDataSource
import com.advice.firebase.data.sources.FirebaseWifiNetworksDataSource
import com.advice.firebase.session.FirebaseUserSession
import com.advice.locations.data.repositories.LocationRepository
import com.advice.locations.presentation.viewmodel.LocationsViewModel
import com.advice.play.AgeSignalsRepository
import com.advice.play.AppManager
import com.advice.play.createAgeSignalsManager
import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.viewmodel.ProductCart
import com.advice.products.presentation.viewmodel.ProductsViewModel
import com.advice.reminder.ReminderManager
import com.advice.retrofit.datasource.RetrofitMapsDataSource
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.FAQRepository
import com.advice.schedule.data.repositories.FeedbackFormRepository
import com.advice.schedule.data.repositories.FiltersRepository
import com.advice.schedule.data.repositories.HomeRepository
import com.advice.schedule.data.repositories.InformationRepository
import com.advice.schedule.data.repositories.MapRepository
import com.advice.schedule.data.repositories.MenuRepository
import com.advice.schedule.data.repositories.NewsRepository
import com.advice.schedule.data.repositories.OrganizationsRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.SearchRepository
import com.advice.schedule.data.repositories.SettingsRepository
import com.advice.schedule.data.repositories.SpeakerRepository
import com.advice.schedule.data.repositories.SpeakersRepository
import com.advice.schedule.data.repositories.TagsRepository
import com.advice.schedule.data.repositories.WifiNetworkRepository
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.presentation.viewmodel.ConferenceViewModel
import com.advice.schedule.presentation.viewmodel.EventViewModel
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
import com.advice.schedule.ui.screens.WifiViewModel
import com.advice.wifi.WirelessConnectionManager
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val APPLICATION_SCOPE = "applicationScope"

val appModule = module {

    single(named(APPLICATION_SCOPE)) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    } withOptions {
        onClose { it?.cancel() }
    }

    single { Storage(get(), get(), BuildConfig.VERSION_CODE) }

    single {
        GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
    }

    single { FirebaseCrashlytics.getInstance() }
    single {
        val cacheSize: Long = 250 * 1024 * 1024 // 250 MB

        val cacheSettings = PersistentCacheSettings.newBuilder()
            .setSizeBytes(cacheSize)
            .build()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(cacheSettings)
            .build()

        FirebaseFirestore.getInstance().apply {
            firestoreSettings = settings
        }
    }
    single { FirebaseAuth.getInstance() }
    single { FirebaseStorage.getInstance() }
    single { FirebaseAnalytics.getInstance(androidContext()) }

    // work manager
    single { WorkManager.getInstance(androidContext()) }

    // update manager
    single { AppManager(androidContext()) }

    single { AnalyticsProvider(get(), BuildConfig.VERSION_CODE) }

    // reminder
    single { NotificationHelper(get()) }
    single { ToastManager() }
    single { ReminderManager(get()) }

    // navigation
    single { NavigationManager() }

    // repo
    single { ScheduleRepository(get(), get(), get(), get(named("tags"))) }
    single { NewsRepository(get()) }
    single { HomeRepository(get(), get(), get(), get(), get(), get(), get()) }
    single { SpeakersRepository(get()) }
    single { ContentRepository(get(), get(), get(), get()) }
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
    single { FeedbackFormRepository(get()) }

//    single<BookmarkedElementDataSource> { BookmarksDataSourceImpl(get(), get()) }
    single<BookmarkedElementDataSource>(named("tags")) { InMemoryBookmarkedDataSourceImpl() }
    single<BookmarkedElementDataSource>(named("events")) {
        SharedPreferencesBookmarkDataSource(
            androidContext(),
        )
    }

    single<UserSession> {
        FirebaseUserSession(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(named(APPLICATION_SCOPE)),
        )
    }
    single<AudiencePolicy> { FailOpenAudiencePolicy() }
    single<NewsDataSource> {
        FirebaseNewsDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
    }
    single<ConferencesDataSource> { FirebaseConferencesDataSource(get()) }
    single<ContentDataSource> {
        FirebaseContentDataSource(
            get(),
            get(),
            get(),
            get(),
            get<LocationsDataSource>(),
            get(),
            get(named("events")),
            get(),
            get(named(APPLICATION_SCOPE)),
        )
    }
    single<TagsDataSource> {
        FirebaseTagsDataSource(get(), get(), get(named("tags")), get(named(APPLICATION_SCOPE)))
    }
    single<FAQDataSource> {
        FirebaseFAQDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
    }
    single<LocationsDataSource> {
        FirebaseLocationsDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
    }
    single {
        RetrofitMapsDataSource(
            get(),
            androidContext().applicationContext.getExternalFilesDir(null),
        )
    } withOptions {
        bind<MapsDataSource>()
        onClose { it?.close() }
    }

    single<SpeakersDataSource> {
        FirebaseSpeakersDataSource(get(), get(), get(), get(named(APPLICATION_SCOPE)))
    }
    single<ProductsDataSource> {
        FirebaseProductsDataSource(get(), get(), get(), get(), get(named(APPLICATION_SCOPE)))
    }

    // Organizations
    single<OrganizationsDataSource> {
        FirebaseOrganizationDataSource(get(), get(), get(), get(named(APPLICATION_SCOPE)))
    }
    single<VendorsDataSource> { FirebaseVendorsDataSource(get(), get()) }
    single<VillagesDataSource> { FirebaseVillagesDataSource(get(), get()) }

    // Documents
    single<DocumentsDataSource> {
        FirebaseDocumentsDataSource(get(), get(), get(), get(named(APPLICATION_SCOPE)))
    }

    single<MenuDataSource> {
        FirebaseMenuDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
    }

    single<FeedbackDataSource> {
        FirebaseFeedbackDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
    }

    // Products
    single<ProductCart> { ProductCart() }

    // Feedback
    single { FeedbackSubmissionRepository(
        "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
        get()
    ) }

    single { WifiNetworkRepository(get()) }
    single<WiFiNetworksDataSource> {
        FirebaseWifiNetworksDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
    }

    // WiFi
    single<WirelessConnectionManager> {
        WirelessConnectionManager(
            androidContext().resources,
            androidContext().getSystemService(WIFI_SERVICE) as WifiManager,
        )
    }

    single<AgeSignalsManager> {
        createAgeSignalsManager(androidContext())
    }
    single { AgeSignalsRepository(get(), get()) }

    viewModel { HomeViewModel() }
    viewModel { ScheduleViewModel() }
    viewModel { EventViewModel() }
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

    viewModel { WifiViewModel() }
}
