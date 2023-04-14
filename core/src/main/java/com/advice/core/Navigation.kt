package com.advice.core

import com.advice.core.local.Merch

interface Navigation {

    fun showMerchSummary()
    fun showMerchItem(merch: Merch)
}