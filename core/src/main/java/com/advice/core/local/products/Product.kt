package com.advice.core.local.products

import android.os.Parcelable
import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Long,
    val code: String,
    val label: String,
    val baseCost: Long,
    val variants: List<ProductVariant>,
    val media: List<ProductMedia>,
    val quantity: Int = 0,
    val cost: Long = baseCost * quantity,
    val tags: List<Tag>,
    val selectedOption: String? = null,
) : Parcelable {

    val inStock: Boolean
        get() = stockStatus != StockStatus.OUT_OF_STOCK

    val stockStatus: StockStatus
        get() {
            if (variants.all { it.stockStatus == StockStatus.OUT_OF_STOCK }) {
                return StockStatus.OUT_OF_STOCK
            }
            if (variants.all {
                    it.stockStatus in listOf(
                        StockStatus.LOW_STOCK,
                        StockStatus.OUT_OF_STOCK,
                    )
                }
            ) {
                return StockStatus.LOW_STOCK
            }
            return StockStatus.IN_STOCK
        }

    val variant: ProductVariant?
        get() = variants.find { it.label == selectedOption }

    val requiresSelection: Boolean
        get() = variants.size > 1

    val variantCost: Long
        get() = baseCost + (variant?.extraCost ?: 0)

    val totalCost: Long
        get() = variantCost * quantity

    fun update(selection: ProductSelection): Product {
        val extraCost = if (selection.selectionOption != null) {
            variants.find { it.label == selection.selectionOption }?.extraCost ?: 0
        } else {
            0
        }

        val cost = (baseCost + extraCost) * selection.quantity
        return copy(
            quantity = selection.quantity,
            selectedOption = selection.selectionOption,
            cost = cost,
        )
    }

    fun hasMedia(): Boolean = media.isNotEmpty()
}
