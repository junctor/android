package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchDataModel(
    val label: String,
    val baseCost: Int,
    val options: List<MerchOption>,
    val image: String? = null,
) : Parcelable

@Parcelize
data class MerchOption(
    val label: String,
    val inStock: Boolean,
    val extraCost: Int,
): Parcelable

fun MerchDataModel.toMerch(
    quantity: Int = 0,
    selectedOption: String? = null,
    discount: Float? = null
): Merch {
    return Merch(
        label,
        label,
        baseCost,
        options,
        image,
        quantity,
        baseCost * quantity,
        discount?.let { baseCost * quantity * (1 - it) }?.toInt(),
        selectedOption
    )
}

data class MerchSelection(
    val id: String,
    val quantity: Int,
    val selectionOption: String?,
)

// in-cart merch
@Parcelize
data class Merch(
    val id: String,
    val label: String,
    val baseCost: Int,
    val options: List<MerchOption>,
    val image: String? = null,
    val quantity: Int = 0,
    val cost: Int = baseCost * quantity,
    val discountedPrice: Int? = null,
    val selectedOption: String? = null,
) : Parcelable {

    val requiresSelection: Boolean = options.isNotEmpty()

    fun update(
        selection: MerchSelection,
        discount: Float?
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