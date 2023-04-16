package com.advice.merch

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Merch
import com.advice.core.local.MerchDataModel
import com.advice.core.local.MerchSelection
import com.advice.core.local.toMerch
import com.advice.core.ui.MerchState
import com.advice.merch.data.MerchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject

class MerchViewModel : ViewModel(), KoinComponent {

    private val repository by inject<MerchRepository>()

    private val selections = mutableListOf<MerchSelection>()

    private val _state = MutableStateFlow(MerchState(emptyList()))
    val state: Flow<MerchState> = _state

    private val _summary = MutableStateFlow(MerchState(emptyList()))
    val summary: Flow<MerchState> = _summary

    var hasDiscount = false
    private val goonDiscount = 0.10f

    init {
        viewModelScope.launch {
            repository.merch.collect {
                _state.value = MerchState(it)
            }
        }
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
        val list = _state.value.elements.map { model ->
            val quantity = selections.filter { it.id == model.label }.sumOf { it.quantity }
            model.copy(quantity = quantity)
        }

        _state.emit(MerchState(list))
    }

    private suspend fun updateSummary() {
        val discount = if (hasDiscount) goonDiscount else null
        // updating the summary broke down based on selections
        val summary = selections.map { selection ->
            val element = _state.value.elements.find { it.label == selection.id }!!
            element.update(selection, discount)
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



