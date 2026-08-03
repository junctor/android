package com.advice.core.local.products

import android.os.Parcelable
import com.advice.core.local.StockStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductVariant(
    val id: Long,
    val label: String,
    val tags: List<Long>,
    val price: Long,
    val stockStatus: StockStatus,
    /** Raw catalog code from Firebase (may or may not be a size). */
    val code: String = "",
) : Parcelable {
    /** Recognized size for filtering, or null when [code] is not a known size. */
    val sizeCode: ProductSizeCode?
        get() = ProductSizeCode.fromCode(code)
}
