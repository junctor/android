package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ProductVariant(
    val label: String,
    val inStock: Boolean,
    val extraCost: Long,
) : Parcelable

data class ProductSelection(
    val id: String,
    val quantity: Int,
    val selectionOption: String?,
)

@Parcelize
data class ProductMedia(
    val url: String,
    val sortOrder: Int,
) : Parcelable

// in-cart Product
@Parcelize
data class Product(
    val id: Long,
    val label: String,
    val baseCost: Long,
    val variants: List<ProductVariant>,
    val media: List<ProductMedia>,
    val quantity: Int = 0,
    val cost: Long = baseCost * quantity,
    val discountedPrice: Int? = null,
    val selectedOption: String? = null,
) : Parcelable {

    val requiresSelection: Boolean = variants.isNotEmpty()

    fun update(
        selection: ProductSelection,
        discount: Float?,
    ): Product {
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
            discountedPrice = if (discount != null) (cost * (1 - discount)).toInt() else null
        )
    }
}