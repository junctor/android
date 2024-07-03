package com.advice.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.products.Product
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

sealed class ProductsScreenState {
    object Loading : ProductsScreenState()
    object Error : ProductsScreenState()
    data class Success(val data: ProductsState) : ProductsScreenState()
}

class ProductsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ProductsRepository>()
    private val storage by inject<Storage>()
    private val cart by inject<ProductCart>()

    private val _state = MutableStateFlow<ProductsScreenState>(ProductsScreenState.Loading)
    val state: Flow<ProductsScreenState> = _state

    private val products = mutableListOf<Product>()
    private val featured = mutableListOf<Product>()
    private var showMerchInformation: Boolean = false
    private var canAdd: Boolean = false
    private var merchDocument: Long? = null

    init {
        showMerchInformation = !storage.hasSeenMerchInformation()

        viewModelScope.launch {
            repository.conference.collect {
                canAdd = it.flags["enable_merch_cart"] ?: false
                merchDocument = it.merchDocumentId
            }
        }
        viewModelScope.launch {
            repository.products.collect { it ->
                products.clear()
                featured.clear()
                products.addAll(it)
                featured.addAll(it.shuffled().filter { it.hasMedia() }.take(5))

                updateList()
                updateSummary()
            }
        }
    }


    fun addToCart(selection: ProductSelection) {
        viewModelScope.launch {
            cart.add(selection)
            updateList()
            updateSummary()
        }
    }

    private suspend fun updateList() {
        // merging the merch list with the selections
        val list = products.map { model ->
            val quantity = cart.getSelections().filter { it.id == model.id }.sumOf { it.quantity }
            model.copy(quantity = quantity)
        }

        updateState(
            products = list,
        )
    }

    private suspend fun updateSummary() {
        // updating the summary broke down based on selections
        val summary = cart.getSelections().map { selection ->
            val element = products.find { it.id == selection.id }!!
            element.update(selection)
        }

        updateState(
            cart = summary,
            json = summary.toJson(),
        )
    }

    fun setQuantity(id: Long, quantity: Int, selectedOption: String?) {
        viewModelScope.launch {
            cart.setQuantity(id, quantity, selectedOption)
            updateList()
            updateSummary()
        }
    }

    fun dismiss() {
        storage.dismissMerchInformation()
        updateState(showMerchInformation = false)
    }

    private fun updateState(
        featured: List<Product> = this.featured,
        products: List<Product> = this.products,
        merchDocument: Long? = this.merchDocument,
        showMerchInformation: Boolean = this.showMerchInformation,
        canAdd: Boolean = this.canAdd,
        cart: List<Product> = emptyList(),
        json: String? = null,
    ) {
        _state.value = ProductsScreenState.Success(
            ProductsState(
                featured = featured,
                products = products,
                merchDocument = merchDocument,
                showMerchInformation = showMerchInformation,
                canAdd = canAdd,
                cart = cart,
                json = json,
            )
        )
    }
}
