package com.shortstack.hackertracker.Analytics

import com.crashlytics.android.answers.CustomEvent
import com.shortstack.hackertracker.Model.Filter

class FilterEvent(filter : Filter) : CustomEvent(AnalyticsController.Analytics.SCHEDULE_FILTERS.toString()) {

    init {
        val array = filter.typesArray

        for (i in array.indices) {
            putCustomAttribute(array[i], true.toString())
        }
    }
}
