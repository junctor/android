package com.advice.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Tag
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.core.utils.Storage
import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.state.ProductsScreenState
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.DismissibleInformation
import com.advice.products.utils.toJson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ProductsViewModel : ViewModel(), KoinComponent {

    private val repository by inject<ProductsRepository>()
    private val storage by inject<Storage>()
    private val cart by inject<ProductCart>()

    private val _state = MutableStateFlow<ProductsScreenState>(ProductsScreenState.Loading)
    val state: Flow<ProductsScreenState> = _state

    private val products = mutableListOf<Product>()
    private var canAdd: Boolean = false
    private var merchDocument: Long? = null
    private var merchMandatoryAcknowledgement: String? = null
    private var merchTaxStatement: String? = null

    init {
        viewModelScope.launch {
            repository.conference.collect {
                canAdd = it.flags["enable_merch_cart"] ?: false
                merchDocument = it.merchInformation?.merchHelpDocId
                merchMandatoryAcknowledgement = it.merchInformation?.merchMandatoryAcknowledgement
                merchTaxStatement = it.merchInformation?.merchTaxStatement
            }
        }
        viewModelScope.launch {
            repository.products.collect {
                products.clear()
                products.addAll(it.sortedByDescending { it.inStock })

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
        val summary = cart.getSelections().mapNotNull { selection ->
            products.find { it.id == selection.id }?.update(selection)
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

    fun dismiss(dismissibleInformation: DismissibleInformation) {
        storage.dismissMerchInformation(dismissibleInformation.key)
        updateState()
    }

    private fun updateState(
        products: List<Product> = this.products,
        merchDocument: Long? = this.merchDocument,
        merchMandatoryAcknowledgement: String? = this.merchMandatoryAcknowledgement,
        merchTaxStatement: String? = this.merchTaxStatement,
        canAdd: Boolean = this.canAdd,
        cart: List<Product> = emptyList(),
        json: String? = null,
    ) {
        val data = ProductsState(
            groups = products.groupBy { it.tags.firstOrNull() ?: Tag(1, "Other", "", "", -1) },
            informationList = getInformationList(),
            merchDocument = merchDocument,
            merchMandatoryAcknowledgement = merchMandatoryAcknowledgement,
            merchTaxStatement = merchTaxStatement,
            canAdd = canAdd,
            cart = cart,
            json = json,
        )
        _state.value = ProductsScreenState.Success(data)
    }

    private fun getInformationList(): MutableList<DismissibleInformation> {
        val list = mutableListOf<DismissibleInformation>()
        // Legal information about sales being cash only and include Nevada State Sales Tax
        val text = merchMandatoryAcknowledgement
        if (!storage.hasSeenMerchInformation("mandatory_acknowledgement") && text != null) {
            list.add(
                DismissibleInformation(
                    key = "mandatory_acknowledgement",
                    text = text,
                    document = null,
                )
            )
        }
        return list
    }
}
