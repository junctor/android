package com.advice.ui.screens

import com.advice.core.local.Event
import com.advice.core.ui.ScheduleFilter

sealed class ScheduleScreenState {
    object Init : ScheduleScreenState()
    object Loading : ScheduleScreenState()
    data class Success(
        val filter: ScheduleFilter,
        val days: Map<String, List<Event>>,
    ) : ScheduleScreenState()

    data class Error(val error: String) : ScheduleScreenState()
}