package com.advice.core.storage

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariantSelection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import timber.log.Timber

class MerchCartStore(
    context: Context,
    private val gson: Gson,
) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(UserPreferencesStore.KEY_PREFERENCES, Context.MODE_PRIVATE)

    fun dismissMerchInformation(key: String) = preferences.edit(commit = true) { putBoolean("merch_information_$key", true) }

    fun hasSeenMerchInformation(key: String): Boolean = preferences.getBoolean("merch_information_$key", false)

    fun setSelectedProducts(
        id: Long,
        list: List<ProductVariantSelection>,
    ) {
        val sanitized = sanitizeSelections(list)
        preferences.edit { putString("merch_products_selection_$id", gson.toJson(sanitized)) }
    }

    fun getSelectedProducts(id: Long): List<ProductVariantSelection> {
        val json = preferences.getString("merch_products_selection_$id", null) ?: return emptyList()
        try {
            val list =
                gson.fromJson<List<ProductVariantSelection>>(
                    json,
                    object : TypeToken<List<ProductVariantSelection>>() {}.type,
                )
            return sanitizeSelections(list.orEmpty())
        } catch (ex: Exception) {
            Timber.e("Could not convert stored merch products selection to product variant selection")
            Timber.e(ex)
            return emptyList()
        }
    }

    /**
     * Drops selections whose product or variant no longer exists in [products], persists the
     * pruned list, and returns it. Out-of-stock items are kept; only missing catalog IDs are removed.
     */
    fun pruneToCatalog(
        conferenceId: Long,
        products: List<Product>,
        selections: List<ProductVariantSelection>,
    ): List<ProductVariantSelection> {
        val pruned = pruneSelectionsToCatalog(selections, products)
        setSelectedProducts(conferenceId, pruned)
        return pruned
    }

    companion object {
        fun sanitizeSelections(list: List<ProductVariantSelection?>): List<ProductVariantSelection> =
            list.mapNotNull { selection ->
                selection?.takeIf { it.quantity > 0 }
            }

        fun pruneSelectionsToCatalog(
            selections: List<ProductVariantSelection>,
            products: List<Product>,
        ): List<ProductVariantSelection> {
            val byId = products.associateBy { it.id }
            return selections.mapNotNull { selection ->
                val product = byId[selection.id] ?: return@mapNotNull null
                val variantId = selection.variant ?: return@mapNotNull null
                if (product.variants.none { it.id == variantId }) {
                    return@mapNotNull null
                }
                selection
            }
        }
    }
}
