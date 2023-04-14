package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MerchDataModel(
    val label: String,
    val baseCost: Int,
    val options: List<String>,
    val hasImage: Boolean = false,
) : Parcelable

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
        hasImage,
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
    val options: List<String>,
    val hasImage: Boolean,
    val quantity: Int = 0,
    val cost: Int = baseCost * quantity,
    val discountedPrice: Int? = null,
    val selectedOption: String? = null,
) : Parcelable {

    val requiresSelection: Boolean = options.isNotEmpty()
}