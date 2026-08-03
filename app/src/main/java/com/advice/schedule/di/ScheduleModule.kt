package com.advice.schedule.di

import com.advice.data.sources.BookmarkDataSourceQualifiers
import com.advice.schedule.data.repositories.ContentRepository
import com.advice.schedule.data.repositories.FAQRepository
import com.advice.schedule.data.repositories.FiltersRepository
import com.advice.schedule.data.repositories.HomeRepository
import com.advice.schedule.data.repositories.InformationRepository
import com.advice.schedule.data.repositories.MapRepository
import com.advice.schedule.data.repositories.MenuRepository
import com.advice.schedule.data.repositories.NewsRepository
import com.advice.schedule.data.repositories.ScheduleRepository
import com.advice.schedule.data.repositories.SearchRepository
import com.advice.schedule.data.repositories.SpeakerRepository
import com.advice.schedule.data.repositories.SpeakersRepository
import com.advice.schedule.data.repositories.TagsRepository
import com.advice.schedule.domain.ContentBookmarkUseCase
import com.advice.schedule.presentation.viewmodel.ConferenceViewModel
import com.advice.schedule.presentation.viewmodel.EventViewModel
import com.advice.schedule.presentation.viewmodel.FAQViewModel
import com.advice.schedule.presentation.viewmodel.FiltersViewModel
import com.advice.schedule.presentation.viewmodel.HomeViewModel
import com.advice.schedule.presentation.viewmodel.InformationViewModel
import com.advice.schedule.presentation.viewmodel.MapsViewModel
import com.advice.schedule.presentation.viewmodel.ScheduleViewModel
import com.advice.schedule.presentation.viewmodel.SearchViewModel
import com.advice.schedule.presentation.viewmodel.SpeakerViewModel
import com.advice.schedule.presentation.viewmodel.SpeakersViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val scheduleModule =
    module {
        single { ContentRepository(get(), get(), get(), get()) }
        single { ContentBookmarkUseCase(get(), get()) }
        single {
            ScheduleRepository(
                get(),
                get(),
                get(),
                get(named(BookmarkDataSourceQualifiers.FILTER_SELECTIONS)),
            )
        }
        single { NewsRepository(get()) }
        single { HomeRepository(get(), get(), get(), get(), get(), get(), get(), get()) }
        single { SpeakersRepository(get()) }
        single { SpeakerRepository(get(), get()) }
        single {
            FiltersRepository(
                get(),
                get(named(BookmarkDataSourceQualifiers.FILTER_SELECTIONS)),
            )
        }
        single { FAQRepository(get()) }
        single { MapRepository(get()) }
        single { InformationRepository(get(), get(), get(), get()) }
        single { TagsRepository(get()) }
        single { SearchRepository(get(), get(), get(), get(), get(), get(), get()) }
        single { MenuRepository(get(), get()) }

        viewModel { HomeViewModel() }
        viewModel { ScheduleViewModel() }
        viewModel { EventViewModel() }
        viewModel { SpeakerViewModel() }
        viewModel { SpeakersViewModel() }
        viewModel { MapsViewModel() }
        viewModel { InformationViewModel() }
        viewModel { FAQViewModel() }
        viewModel { FiltersViewModel() }
        viewModel { ConferenceViewModel() }
        viewModel { SearchViewModel() }
    }
