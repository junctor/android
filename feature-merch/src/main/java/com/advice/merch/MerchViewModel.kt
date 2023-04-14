package com.advice.merch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Merch
import com.advice.core.local.MerchDataModel
import com.advice.core.local.MerchSelection
import com.advice.core.local.toMerch
import com.advice.core.ui.MerchState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

val data = listOf(
    MerchDataModel(
        "DC30 Homecoming Men's T-Shirt",
        35,
        listOf("S", "4XL", "5XL", "6XL"),
        hasImage = true
    ),
    MerchDataModel("DC30 Homecoming Women's T-Shirt", 35, listOf("XS", "S", "L", "XL")),
    MerchDataModel(
        "DC30 Square Men's T-Shirt",
        35,
        listOf("S", "4XL", "5XL", "6XL"),
        hasImage = true,
    ),
    MerchDataModel(
        "DC30 Square Women's T-Shirt",
        35,
        listOf("S", "4XL", "5XL", "6XL"),
        hasImage = true
    ),
    MerchDataModel("DC30 Skull T-Shirt", 40, listOf("S", "4XL", "5XL", "6XL")),
    MerchDataModel("DC30 Signal T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
    MerchDataModel("DC30 Crown T-Shirt", 50, listOf("S", "4XL", "5XL", "6XL")),
    MerchDataModel("Pride T-Shirt", 35, listOf("S", "4XL", "5XL", "6XL")),
    MerchDataModel("D I S O B E Y Pin", 10, listOf(), hasImage = true),
)

class MerchViewModel : ViewModel() {

    private val selections = mutableListOf<MerchSelection>()

    private val _state = MutableStateFlow(MerchState(emptyList()))
    val state: Flow<MerchState> = _state

    private val _summary = MutableStateFlow(MerchState(emptyList()))
    val summary: Flow<MerchState> = _summary

    var hasDiscount = false
    private val goonDiscount = 0.10f

    init {
        val merch = data.map { it.toMerch() }
        _state.value = MerchState(merch)
    }

    fun addToCart(selection: MerchSelection) {
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
        val list = data.map { model ->
            val quantity = selections.filter { it.id == model.label }.sumOf { it.quantity }
            model.toMerch(quantity = quantity)
        }

        _state.emit(MerchState(list))
    }

    private suspend fun updateSummary() {
        // updating the summary broke down based on selections
        val summary = selections.map { selection ->
            val element = data.find { it.label == selection.id }!!
            element.toMerch(
                selection.quantity,
                selection.selectionOption,
                if (hasDiscount) goonDiscount else null
            )
        }

        _summary.emit(MerchState(summary, hasDiscount = hasDiscount))
    }

    fun setQuantity(id: String, quantity: Int, selectedOption: String?) {
        viewModelScope.launch {
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



