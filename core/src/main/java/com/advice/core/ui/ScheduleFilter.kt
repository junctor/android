package com.advice.core.ui

sealed class ScheduleFilter {
    object Default : ScheduleFilter()
    data class Location(val id: Long?) : ScheduleFilter()
    data class Tag(val id: Long?) : ScheduleFilter()
    data class Tags(val ids: List<Long>?) : ScheduleFilter()
}
