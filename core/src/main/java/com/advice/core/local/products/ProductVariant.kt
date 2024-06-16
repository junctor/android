package com.advice.core.local.products

import android.os.Parcelable
import com.advice.core.local.StockStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductVariant(
    val id: Long,
    val label: String,
    val tags: List<Long>,
    val extraCost: Long,
    val stockStatus: StockStatus,
) : Parcelable
