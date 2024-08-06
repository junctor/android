package com.advice.core.ui

sealed class ScheduleFilter {
    data object Default : ScheduleFilter()
    data class Location(val id: Long?) : ScheduleFilter()
    data class Tag(val id: Long?, val label: String?) : ScheduleFilter()
    data class Tags(val ids: List<Long>?) : ScheduleFilter()
}
