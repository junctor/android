package com.advice.core.local

enum class ScheduleDayFormat(
    val id: String,
    val pattern: String,
) {
    MonthDay(id = "month_day", pattern = "MMMM d"),
    DayOfWeek(id = "day_of_week", pattern = "EEEE"),
    DayAbbrMonthDay(id = "day_abbr_month_day", pattern = "EEE, MMM d"),
    ;

    companion object {
        fun fromId(id: String?): ScheduleDayFormat = entries.firstOrNull { it.id == id } ?: MonthDay
    }
}
