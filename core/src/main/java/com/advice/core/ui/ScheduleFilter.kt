package com.advice.core.ui

sealed class ScheduleFilter {
    object Default : ScheduleFilter()
    data class Location(val id: String?) : ScheduleFilter()
    data class Tag(val id: String?) : ScheduleFilter()
    data class Tags(val ids: List<String>) : ScheduleFilter()
}
