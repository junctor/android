package com.advice.schedule.di

import com.advice.data.di.APPLICATION_SCOPE
import com.advice.data.sources.BookmarkDataSourceQualifiers
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.FiltersRepository
import com.advice.schedule.data.repositories.HomeRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.TagsRepository
import com.advice.schedule.domain.ContentBookmarkUseCase
import com.advice.schedule.presentation.viewmodel.ConferenceViewModel
import com.advice.schedule.presentation.viewmodel.ContentViewModel
import com.advice.schedule.presentation.viewmodel.EventViewModel
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.telemetry.TelemetryCollection
import com.advice.settings.BookmarkedReminderRescheduler
import com.advice.settings.TelemetryApplier
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scheduleModule =
    module {
        single {
            ContentRepository(
                get(),
                get(),
                get(),
                get(),
                get(named(APPLICATION_SCOPE)),
            )
        }
        single { ContentBookmarkUseCase(get(), get()) }
        single {
            ScheduleRepository(
                get(),
                get(),
                get(),
                get(named(BookmarkDataSourceQualifiers.FILTER_SELECTIONS)),
            )
        }
        single { HomeRepository(get(), get(), get(), get(), get(), get(), get(), get()) }
        single {
            FiltersRepository(
                get(),
                get(named(BookmarkDataSourceQualifiers.FILTER_SELECTIONS)),
                get(named(APPLICATION_SCOPE)),
            )
        }
        single { TagsRepository(get()) }

        single<BookmarkedReminderRescheduler> {
            BookmarkedReminderRescheduler { get<ContentBookmarkUseCase>().rescheduleBookmarkedReminders() }
        }
        single<TelemetryApplier> {
            TelemetryApplier { context, preferences -> TelemetryCollection.apply(context, preferences) }
        }

        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { ScheduleViewModel(get(), get(), get()) }
        viewModel { EventViewModel(get(), get()) }
        viewModel { ContentViewModel(get(), get()) }
        viewModel { FiltersViewModel(get()) }
        viewModel { ConferenceViewModel(get()) }
    }
