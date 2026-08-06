package com.advice.firebase.di

import com.advice.core.audience.AudiencePolicy
import com.advice.core.audience.FailOpenAudiencePolicy
import com.advice.data.di.APPLICATION_SCOPE
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkDataSourceQualifiers
import com.advice.data.sources.ConferencesDataSource
import com.advice.data.sources.ContentDataSource
import com.advice.data.sources.DocumentsDataSource
import com.advice.data.sources.FAQDataSource
import com.advice.data.sources.FeedbackDataSource
import com.advice.data.sources.LocationsDataSource
import com.advice.data.sources.MenuDataSource
import com.advice.data.sources.NewsDataSource
import com.advice.data.sources.OrganizationsDataSource
import com.advice.data.sources.ProductsDataSource
import com.advice.data.sources.SpeakersDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.data.sources.WiFiNetworksDataSource
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
import com.advice.firebase.data.sources.FirebaseWifiNetworksDataSource
import com.advice.firebase.session.FirebaseUserSession
import com.advice.firebase.telemetry.firestoreTelemetry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.storage.FirebaseStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val firebaseDataModule =
    module {
        single {
            val cacheSize: Long = 250 * 1024 * 1024 // 250 MB

            val cacheSettings =
                PersistentCacheSettings
                    .newBuilder()
                    .setSizeBytes(cacheSize)
                    .build()

            val settings =
                FirebaseFirestoreSettings
                    .Builder()
                    .setLocalCacheSettings(cacheSettings)
                    .build()

            FirebaseFirestore.getInstance().apply {
                firestoreSettings = settings
            }
        }
        single { FirebaseAuth.getInstance() }
        single { FirebaseStorage.getInstance() }

        // Same instance the snapshot flow helpers record into.
        single { firestoreTelemetry }

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
                get(named(BookmarkDataSourceQualifiers.EVENT_BOOKMARKS)),
                get(),
                get(named(APPLICATION_SCOPE)),
            )
        }
        single<TagsDataSource> {
            FirebaseTagsDataSource(
                get(),
                get(),
                get(named(BookmarkDataSourceQualifiers.FILTER_SELECTIONS)),
                get(named(APPLICATION_SCOPE)),
            )
        }
        single<FAQDataSource> {
            FirebaseFAQDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
        }
        single<LocationsDataSource> {
            FirebaseLocationsDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
        }

        single<SpeakersDataSource> {
            FirebaseSpeakersDataSource(get(), get(), get(), get(named(APPLICATION_SCOPE)))
        }
        single<ProductsDataSource> {
            FirebaseProductsDataSource(get(), get(), get(), get(), get(named(APPLICATION_SCOPE)))
        }

        single<OrganizationsDataSource> {
            FirebaseOrganizationDataSource(get(), get(), get(), get(named(APPLICATION_SCOPE)))
        }

        single<DocumentsDataSource> {
            FirebaseDocumentsDataSource(get(), get(), get(), get(named(APPLICATION_SCOPE)))
        }

        single<MenuDataSource> {
            FirebaseMenuDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
        }

        single<FeedbackDataSource> {
            FirebaseFeedbackDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
        }

        single<WiFiNetworksDataSource> {
            FirebaseWifiNetworksDataSource(get(), get(), get(named(APPLICATION_SCOPE)))
        }
    }
