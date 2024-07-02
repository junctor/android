package com.advice.products.presentation.viewmodel

import com.advice.core.local.products.ProductSelection

class ProductList {

    private val selections = mutableListOf<ProductSelection>()

    fun add(selection: ProductSelection) {
        // Look to see if it already exists in our selection
        val indexOf =
            selections.indexOfFirst { it.id == selection.id && it.selectionOption == selection.selectionOption }
        if (indexOf != -1) {
            // add the two together
            selections[indexOf] =
                selections[indexOf].copy(quantity = selections[indexOf].quantity + selection.quantity)
        } else {
            // add to the end of the list
            selections.add(selection)
        }
    }

    fun setQuantity(id: Long, quantity: Int, selectedOption: String?) {
        val indexOf =
            selections.indexOfFirst { it.id == id && it.selectionOption == selectedOption }
        if (indexOf != -1) {
            val element = selections[indexOf]
            if (quantity == 0) {
                selections.removeAt(indexOf)
            } else {
                selections[indexOf] = element.copy(quantity = quantity)
            }
        }
    }

    fun getSelections(): List<ProductSelection> {
        return selections
    }
}
