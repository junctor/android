package com.advice.merch.presentation.viewmodel

import com.advice.core.local.StockStatus
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariantSelection
import com.advice.merch.ui.components.DismissibleInformation
import com.advice.merch.utils.toStringData

internal const val MANDATORY_ACKNOWLEDGEMENT_KEY = "mandatory_acknowledgement"

/**
 * Joins persisted cart selections to the current catalog so UI lines pick up live
 * [com.advice.core.local.products.ProductVariant.stockStatus] (e.g. after an OOS refresh).
 */
internal fun resolveCartSelections(
    products: List<Product>,
    selections: List<ProductVariantSelection>,
): List<ProductSelection> =
    selections.mapNotNull { selection ->
        val product = products.find { it.id == selection.id } ?: return@mapNotNull null
        val variant =
            product.variants.find { it.id == selection.variant } ?: return@mapNotNull null
        ProductSelection(product, variant, selection.quantity)
    }

/** Compact-order QR payload; out-of-stock lines are omitted by [toStringData]. */
internal fun cartQrPayload(
    cart: List<ProductSelection>,
    conference: Long?,
    versionCode: Int,
): String? = cart.toStringData(conference = conference, versionCode = versionCode)

/** Subtotal in cents; out-of-stock lines are excluded. */
internal fun cartSubtotalCents(list: List<ProductSelection>): Long =
    list
        .filter { it.variant.stockStatus != StockStatus.OUT_OF_STOCK }
        .sumOf { it.cost }

/** Mandatory merch acknowledgement banner when unseen and non-blank. */
internal fun mandatoryAcknowledgementInfo(
    text: String?,
    hasSeen: Boolean,
): List<DismissibleInformation> {
    if (hasSeen || text.isNullOrBlank()) return emptyList()
    return listOf(
        DismissibleInformation(
            key = MANDATORY_ACKNOWLEDGEMENT_KEY,
            text = text,
            document = null,
        ),
    )
}
