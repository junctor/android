package com.advice.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.ProductSelection
import com.advice.core.ui.ProductsState
import com.advice.products.data.ProductsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class ProductsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ProductsRepository>()

    private val selections = mutableListOf<ProductSelection>()

    private val _state = MutableStateFlow(ProductsState(emptyList()))
    val state: Flow<ProductsState> = _state

    private val _summary = MutableStateFlow(ProductsState(emptyList()))
    val summary: Flow<ProductsState> = _summary

    private var hasDiscount = false
    private val goonDiscount = 0.10f

    init {
        viewModelScope.launch {
            repository.products.collect {
                _state.value = ProductsState(it)
            }
        }
    }

    fun addToCart(selection: ProductSelection) {
        viewModelScope.launch {
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

            updateList()
            updateSummary()
        }
    }

    private suspend fun updateList() {
        // merging the merch list with the selections
        val list = _state.value.elements.map { model ->
            val quantity = selections.filter { it.id == model.label }.sumOf { it.quantity }
            model.copy(quantity = quantity)
        }

        _state.emit(ProductsState(list))
    }

    private suspend fun updateSummary() {
        val discount = if (hasDiscount) goonDiscount else null
        // updating the summary broke down based on selections
        val summary = selections.map { selection ->
            val element = _state.value.elements.find { it.label == selection.id }!!
            element.update(selection, discount)
        }

        _summary.emit(ProductsState(summary, hasDiscount = hasDiscount))
    }

    fun setQuantity(id: Long, quantity: Int, selectedOption: String?) {
        viewModelScope.launch {
            val indexOf =
                selections.indexOfFirst { it.id == id.toString() && it.selectionOption == selectedOption }
            if (indexOf != -1) {
                val element = selections[indexOf]
                if (quantity == 0) {
                    selections.removeAt(indexOf)
                } else {
                    selections[indexOf] = element.copy(quantity = quantity)
                }
            }

            updateList()
            updateSummary()
        }
    }

    fun applyDiscount(isChecked: Boolean) {
        viewModelScope.launch {
            hasDiscount = isChecked
            updateSummary()
        }
    }
}



