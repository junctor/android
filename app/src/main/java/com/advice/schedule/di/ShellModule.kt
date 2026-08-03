package com.advice.schedule.di

import androidx.work.WorkManager
import com.advice.core.utils.ToastManager
import com.advice.data.InMemoryBookmarkedDataSourceImpl
import com.advice.data.SharedPreferencesBookmarkDataSource
import com.advice.data.di.APPLICATION_SCOPE
import com.advice.data.sources.BookmarkDataSourceQualifiers
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.storage.ContentSyncStore
import com.advice.data.storage.UserPreferencesStore
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.offline.OfflineQueueConnectivityMonitor
import com.advice.schedule.presentation.viewmodel.MainViewModel
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.onClose
import org.koin.core.module.dsl.viewModel
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
        single { ContentSyncStore(androidContext()) }

        single {
            GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        }

        single { FirebaseCrashlytics.getInstance() }

        single { WorkManager.getInstance(androidContext()) }

        single { ToastManager() }

        single { NavigationManager() }

        single {
            OfflineQueueConnectivityMonitor(
                androidContext(),
                get(named(APPLICATION_SCOPE)),
                get(),
                get(),
            )
        }

        viewModel {
            MainViewModel(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
            )
        }

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
