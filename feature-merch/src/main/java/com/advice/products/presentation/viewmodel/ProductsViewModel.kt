package com.advice.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariant
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
    private val productVariantTags = mutableListOf<TagType>()
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
        viewModelScope.launch {
            repository.variants.collect {
                productVariantTags.clear()
                productVariantTags.addAll(it)
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

    fun setQuantity(id: Long, quantity: Int, selectedOption: ProductVariant?) {
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

    fun onTagClicked(tag: Tag) {
        viewModelScope.launch {
            val tagTypes = productVariantTags.map {
                val tags = it.tags.map {
                    if (it.id == tag.id) {
                        it.copy(isSelected = !it.isSelected)
                    } else {
                        it
                    }
                }
                it.copy(tags = tags)
            }
            productVariantTags.clear()
            productVariantTags.addAll(tagTypes)

            updateState(
                productVariantTags = tagTypes,
            )
        }
    }

    /**
     * Updates the state of the screen.
     *
     * Note: Since we have local copies of these lists, if they're updated and used - the view will NOT update.
     */
    private fun updateState(
        products: List<Product> = this.products,
        productVariantTags: List<TagType> = this.productVariantTags,
        merchDocument: Long? = this.merchDocument,
        merchMandatoryAcknowledgement: String? = this.merchMandatoryAcknowledgement,
        merchTaxStatement: String? = this.merchTaxStatement,
        canAdd: Boolean = this.canAdd,
        cart: List<Product> = emptyList(),
        json: String? = null,
    ) {
        val filter = productVariantTags.flatMap { it.tags }.filter { it.isSelected }

        val filteredProducts: List<Product> = getFilteredProducts(products, filter)

        val data = ProductsState(
            groups = filteredProducts.groupBy {
                it.tags.firstOrNull() ?: Tag(
                    1,
                    "Other",
                    "",
                    "",
                    -1
                )
            },
            productVariantTagTypes = productVariantTags,
            informationList = getInformationList(),
            merchDocument = merchDocument,
            merchMandatoryAcknowledgement = merchMandatoryAcknowledgement,
            merchTaxStatement = merchTaxStatement,
            canAdd = canAdd,
            cart = cart,
            json = json,
        )

        _state.tryEmit(ProductsScreenState.Success(data))
    }

    private fun getFilteredProducts(products: List<Product>, filter: List<Tag>): List<Product> {
        if (filter.isEmpty()) {
            return products
        }

        return products.map { product ->
            // If there is no variants - return the product as is
            if (!product.requiresSelection) {
                return@map product
            }
            // Check the variants if they're in stock
            val inStock = product.variants.any { variant ->
                filter.any { it.id in variant.tags && variant.stockStatus == StockStatus.IN_STOCK }
            }
            // Override the stock status with our preference
            if (inStock) {
                product.copy(stockStatusOverride = StockStatus.IN_STOCK)
            } else {
                product.copy(stockStatusOverride = StockStatus.OUT_OF_STOCK)
            }
        }.sortedWith(compareBy({ it.stockStatus }, { it.sortOrder }))
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
