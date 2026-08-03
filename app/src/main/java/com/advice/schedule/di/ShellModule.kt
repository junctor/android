package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.storage.ContentSyncStore
import com.advice.core.storage.MerchCartStore
import com.advice.core.storage.OfflineQueueStore
import com.advice.core.storage.UserPreferencesStore
import com.advice.core.utils.ToastManager
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import com.advice.data.SharedPreferencesBookmarkDataSource
import com.advice.data.sources.BookmarkDataSourceQualifiers
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.schedule.navigation.NavigationManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.shortstack.hackertracker.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.withOptions
import org.koin.core.qualifier.named
import org.koin.dsl.module

val shellModule =
    module {
        single(named(APPLICATION_SCOPE)) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        } withOptions {
            onClose { it?.cancel() }
        }

        single { UserPreferencesStore(androidContext()) }
        single { MerchCartStore(androidContext(), get()) }
        single { ContentSyncStore(androidContext()) }
        single { OfflineQueueStore(androidContext(), get()) }

        single {
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        }

        single { FirebaseCrashlytics.getInstance() }
        single { FirebaseAnalytics.getInstance(androidContext()) }

        single { WorkManager.getInstance(androidContext()) }

        single { AnalyticsProvider(get(), BuildConfig.VERSION_CODE) }

        single { ToastManager() }

        single { NavigationManager() }

        // Filter tag selections (in-memory). Cleared by "Clear filters".
        single<BookmarkedElementDataSource>(named(BookmarkDataSourceQualifiers.FILTER_SELECTIONS)) {
            InMemoryBookmarkedDataSourceImpl()
        }
        // Persisted session/content favorites. Must not be cleared with filters.
        single<BookmarkedElementDataSource>(named(BookmarkDataSourceQualifiers.EVENT_BOOKMARKS)) {
            SharedPreferencesBookmarkDataSource(
                androidContext(),
            )
        }
    }
