package com.advice.products.presentation.viewmodel

import com.advice.core.local.products.ProductVariantSelection

class ProductCart {

    private val selections = mutableListOf<ProductVariantSelection>()

    fun add(selection: ProductVariantSelection) {
        // Look to see if it already exists in our selection
        val indexOf =
            selections.indexOfFirst { it.id == selection.id && it.variant == selection.variant }
        if (indexOf != -1) {
            // add the two together
            selections[indexOf] =
                selections[indexOf].copy(quantity = selections[indexOf].quantity + selection.quantity)
        } else {
            // add to the end of the list
            selections.add(selection)
        }
    }

    fun setQuantity(id: Long, quantity: Int, variant: Long?) {
        val indexOf =
            selections.indexOfFirst { it.id == id && it.variant == variant }
        if (indexOf != -1) {
            val element = selections[indexOf]
            if (quantity == 0) {
                selections.removeAt(indexOf)
            } else {
                selections[indexOf] = element.copy(quantity = quantity)
            }
        }
    }

    fun clear() {
        selections.clear()
    }

    fun getSelections(): List<ProductVariantSelection> {
        return selections
    }
}
