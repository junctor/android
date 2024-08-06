package com.advice.ui.states

import com.advice.core.local.Event
import com.advice.core.ui.ScheduleFilter

sealed class ScheduleScreenState {
    data object Loading : ScheduleScreenState()

    data class Success(
        val filter: ScheduleFilter,
        val days: Map<String, List<Event>>,
        val showFab: Boolean,
    ) : ScheduleScreenState()

    data class Empty(
        val message: String,
    ) : ScheduleScreenState()

    data class Error(
        val error: String,
    ) : ScheduleScreenState()
}
