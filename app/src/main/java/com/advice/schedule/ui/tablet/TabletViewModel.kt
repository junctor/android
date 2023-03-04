package com.advice.schedule.ui.tablet

import androidx.lifecycle.ViewModel
import com.advice.schedule.repository.FiltersRepository
import com.advice.schedule.repository.HomeRepository
import com.advice.schedule.repository.ScheduleRepository
import kotlinx.coroutines.flow.combine
import org.koin.core.KoinComponent
import org.koin.core.inject

class TabletViewModel : ViewModel(), KoinComponent {

    private val scheduleRepository by inject<ScheduleRepository>()
    private val homeRepository by inject<HomeRepository>()
    private val filtersRepository by inject<FiltersRepository>()


    val state = combine(homeRepository.contents, scheduleRepository.list, filtersRepository.tags) { home, schedule, tags ->
        WideScreenState(home.conference, home.article, schedule, tags)
    }
}