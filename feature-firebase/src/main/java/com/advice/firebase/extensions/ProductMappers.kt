package com.advice.firebase.extensions

import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.firebase.models.products.FirebaseProduct
import com.advice.firebase.models.products.FirebaseProductMedia
import com.advice.firebase.models.products.FirebaseProductVariant
import timber.log.Timber

fun FirebaseProduct.toMerch(tagTypes: List<TagType>): Product? =
    try {
        val defaultTags = listOf(Tag(1, "Other", "", "", -1))
        val productTags = tagIds.mapNotNull { id ->
            tagTypes.flatMap { it.tags }.find { it.id == id }
        }
        Product(
            id = id,
            code = code,
            sortOrder = sortOrder,
            label = title,
            baseCost = priceMin,
            variants = variants.sortedWith(compareBy({ it.stockStatus }, { it.sortOrder }))
                .mapNotNull { it.toMerchOption() },
            media = media.sortedBy { it.sortOrder }.mapNotNull { it.toProductMedia() },
            tags = productTags.ifEmpty { defaultTags },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Merch: ${ex.message}")
        null
    }

fun FirebaseProductVariant.toMerchOption(): ProductVariant? =
    try {
        ProductVariant(
            id = variantId,
            label = title,
            tags = tagIds,
            price = price,
            stockStatus = StockStatus.fromString(stockStatus) ?: StockStatus.IN_STOCK,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to MerchOption: ${ex.message}")
        null
    }

fun FirebaseProductMedia.toProductMedia(): ProductMedia? =
    try {
        ProductMedia(
            url = url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to ProductMedia: ${ex.message}")
        null
    }
