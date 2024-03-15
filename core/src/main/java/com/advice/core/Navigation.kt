package com.advice.core

import com.advice.core.local.products.Product

interface Navigation {

    fun showSchedule(location: Long)

    fun showMerch()
    fun showMerchSummary()
    fun showMerchItem(product: Product)
}
