package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchDataModel(
    val label: String,
    val baseCost: Long,
    val options: List<MerchOption>,
    val image: String? = null,
) : Parcelable

@Parcelize
data class MerchOption(
    val label: String,
    val inStock: Boolean,
    val extraCost: Long,
) : Parcelable

fun MerchDataModel.toMerch(
    quantity: Int = 0,
    selectedOption: String? = null,
    discount: Float? = null,
): Merch {
    TODO()
}

data class MerchSelection(
    val id: String,
    val quantity: Int,
    val selectionOption: String?,
)

@Parcelize
data class ProductMedia(
    val url: String,
    val sortOrder: Int,
) : Parcelable

// in-cart merch
@Parcelize
data class Merch(
    val id: Long,
    val label: String,
    val baseCost: Long,
    val options: List<MerchOption>,
    val media: List<ProductMedia>,
    val quantity: Int = 0,
    val cost: Long = baseCost * quantity,
    val discountedPrice: Int? = null,
    val selectedOption: String? = null,
) : Parcelable {

    val requiresSelection: Boolean = options.isNotEmpty()

    fun update(
        selection: MerchSelection,
        discount: Float?,
    ): Merch {
        val extraCost = if (selection.selectionOption != null) {
            options.find { it.label == selection.selectionOption }?.extraCost ?: 0
        } else {
            0
        }

        val cost = (baseCost + extraCost) * selection.quantity
        val discountedPrice1 = if (discount != null) (cost * (1 - discount)).toInt() else null
        return copy(
            quantity = selection.quantity,
            selectedOption = selection.selectionOption,
            cost = cost,
            discountedPrice = discountedPrice1
        )
    }

}