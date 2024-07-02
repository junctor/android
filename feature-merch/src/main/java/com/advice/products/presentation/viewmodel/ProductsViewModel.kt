package com.advice.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.products.ProductSelection
import com.advice.core.utils.Storage
import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.state.ProductsState
import com.advice.products.utils.toJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ProductsRepository>()
    private val storage by inject<Storage>()

    private val list by inject<ProductList>()

    private val _state = MutableStateFlow(ProductsState.EMPTY)
    val state: Flow<ProductsState> = _state

    init {
        _state.value = _state.value.copy(showMerchInformation = !storage.hasSeenMerchInformation())

        viewModelScope.launch {
            repository.conference.collect {
                _state.value = _state.value.copy(canAdd = it.flags["enable_merch_cart"] ?: false, merchDocument = it.merchDocumentId)
            }
        }
        viewModelScope.launch {
            repository.products.collect {
                val shuffled = it.shuffled().filter { it.hasMedia() }
                val featured = shuffled.take(5)

                _state.value = _state.value.copy(featured = featured, products = it)

                updateList()
                updateSummary()
            }
        }
    }

    fun addToCart(selection: ProductSelection) {
        viewModelScope.launch {
            list.add(selection)
            updateList()
            updateSummary()
        }
    }

    private suspend fun updateList() {
        // merging the merch list with the selections
        val list = _state.value.products.map { model ->
            val quantity = list.getSelections().filter { it.id == model.id }.sumOf { it.quantity }
            model.copy(quantity = quantity)
        }

        _state.emit(_state.value.copy(products = list))
    }

    private suspend fun updateSummary() {
        // updating the summary broke down based on selections
        val summary = list.getSelections().map { selection ->
            val element = _state.value.products.find { it.id == selection.id }!!
            element.update(selection)
        }

        _state.emit(
            _state.value.copy(
                cart = summary,
                json = summary.toJson(),
            )
        )
    }

    fun setQuantity(id: Long, quantity: Int, selectedOption: String?) {
        viewModelScope.launch {
            list.setQuantity(id, quantity, selectedOption)
            updateList()
            updateSummary()
        }
    }

    fun dismiss() {
        storage.dismissMerchInformation()
        _state.value = _state.value.copy(showMerchInformation = false)
    }
}
