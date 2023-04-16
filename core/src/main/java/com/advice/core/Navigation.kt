package com.advice.core

import com.advice.core.local.LocationRow
import com.advice.core.local.Merch

interface Navigation {

    fun showSchedule(location: Long)

    fun showMerch()
    fun showMerchSummary()
    fun showMerchItem(merch: Merch)

}