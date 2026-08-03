package com.advice.merch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductVariantSelection
import com.advice.data.session.UserSession
import com.advice.merch.data.repositories.ProductsRepository
import com.advice.merch.presentation.state.ProductsScreenState
import com.advice.merch.presentation.state.ProductsState
import com.advice.merch.storage.MerchCartStore
import com.advice.merch.ui.components.DismissibleInformation
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import timber.log.Timber

class ProductsViewModel(
    private val repository: ProductsRepository,
    private val merchCartStore: MerchCartStore,
    private val cart: ProductCart,
    private val versionCode: Int,
    private val userSession: UserSession,
) : ViewModel() {
    private val _state = MutableStateFlow<ProductsScreenState>(ProductsScreenState.Loading)
    val state: Flow<ProductsScreenState> = _state

    private val products = mutableListOf<Product>()
    private val productVariantTags = mutableListOf<TagType>()

    private var conference: Long? = null
    private var canAdd: Boolean = false
    private var merchDocument: Long? = null
    private var merchMandatoryAcknowledgement: String? = null
    private var merchTaxStatement: String? = null

    private var observeJob: Job? = null
    private var hasLoadError = false

    init {
        observe()
    }

    /**
     * Re-subscribe to merch data after an error.
     */
    fun retry() {
        hasLoadError = false
        userSession.currentConference?.let { userSession.setConference(it) }
        observe()
    }

    private fun observe() {
        observeJob?.cancel()
        hasLoadError = false
        _state.value = ProductsScreenState.Loading
        observeJob =
            viewModelScope.launch {
                launch {
                    repository.conference
                        .catch { emitError("conference", it) }
                        .collect {
                            if (hasLoadError) return@collect
                            if (it.id != conference) {
                                loadProductSelections(it)
                            }

                            conference = it.id
                            canAdd = it.flags["enable_merch_cart"] ?: false
                            merchDocument = it.merchInformation?.merchHelpDocId
                            merchMandatoryAcknowledgement =
                                it.merchInformation?.merchMandatoryAcknowledgement
                            merchTaxStatement = it.merchInformation?.merchTaxStatement
                            if (products.isNotEmpty()) {
                                updateState()
                            }
                        }
                }
                launch {
                    repository.products
                        .catch { emitError("products", it) }
                        .collect { result ->
                            if (hasLoadError) return@collect
                            when (result) {
                                FlowResult.Loading -> {
                                    if (products.isEmpty()) {
                                        _state.value = ProductsScreenState.Loading
                                    }
                                }
                                is FlowResult.Failure -> {
                                    emitError("products", result.error)
                                }
                                is FlowResult.Success -> {
                                    products.clear()
                                    products.addAll(
                                        result.value.sortedByDescending { product -> product.inStock },
                                    )
                                    syncSizeFiltersFromProducts()
                                    updateSummary()
                                }
                            }
                        }
                }
            }
    }

    /**
     * Size chips are derived from recognized [com.advice.core.local.products.ProductSizeCode]
     * values on variant codes (DEF CON catalogs leave merch-variant tag_ids empty).
     */
    private fun syncSizeFiltersFromProducts() {
        val selected =
            productVariantTags
                .flatMap { it.tags }
                .filter { it.isSelected }
                .map { it.label }
                .toSet()
        productVariantTags.clear()
        sizeFilterTagType(products, selected)?.let { productVariantTags.add(it) }
    }

    private fun emitError(
        source: String,
        throwable: Throwable,
    ) {
        Timber.e(throwable, "Failed to load merch $source")
        hasLoadError = true
        _state.value = ProductsScreenState.Error
    }

    /**
     * Loading any previously selected products.
     */
    private fun loadProductSelections(conference: Conference) {
        cart.clear()
        val selections = merchCartStore.getSelectedProducts(conference.id)
        for (selection in selections) {
            cart.add(selection)
        }
    }

    /**
     * Saving the product selections to local storage to handle restarts.
     */
    private fun saveProductSelection(selections: List<ProductVariantSelection>) {
        conference?.let {
            merchCartStore.setSelectedProducts(it, selections)
        }
    }

    fun addToCart(selection: ProductVariantSelection) {
        viewModelScope.launch {
            cart.add(selection)
            updateSummary()
        }
    }

    private fun updateSummary() {
        var selections = cart.getSelections()
        val conferenceId = conference
        if (conferenceId != null && products.isNotEmpty()) {
            val pruned = merchCartStore.pruneToCatalog(conferenceId, products, selections)
            if (pruned != selections) {
                cart.clear()
                pruned.forEach { cart.add(it) }
                selections = pruned
            }
        } else {
            saveProductSelection(selections)
        }
        updateState(selections = selections)
    }

    fun setQuantity(
        id: Long,
        quantity: Int,
        variant: Long?,
    ) {
        viewModelScope.launch {
            cart.setQuantity(id, quantity, variant)
            updateSummary()
        }
    }

    fun dismiss(dismissibleInformation: DismissibleInformation) {
        merchCartStore.dismissMerchInformation(dismissibleInformation.key)
        updateState()
    }

    fun onTagClicked(tag: Tag) {
        val tagTypes =
            productVariantTags.map { type ->
                val tags =
                    type.tags.map { productTag ->
                        if (productTag.id == tag.id) {
                            productTag.copy(isSelected = !productTag.isSelected)
                        } else {
                            productTag
                        }
                    }
                type.copy(tags = tags)
            }
        productVariantTags.clear()
        productVariantTags.addAll(tagTypes)
        updateState(productVariantTags = tagTypes)
    }

    fun clearFilters() {
        val tagTypes =
            productVariantTags.map { type ->
                type.copy(tags = type.tags.map { it.copy(isSelected = false) })
            }
        productVariantTags.clear()
        productVariantTags.addAll(tagTypes)
        updateState(productVariantTags = tagTypes)
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
        selections: List<ProductVariantSelection> = this.cart.getSelections(),
    ) {
        val filter = productVariantTags.flatMap { it.tags }.filter { it.isSelected }

        val filteredProducts: List<Product> = getFilteredProducts(products, filter)

        val cart = resolveCartSelections(products, selections)

        if (hasLoadError) return

        val state =
            ProductsState(
                groups = groupProducts(filteredProducts),
                productVariantTagTypes = productVariantTags,
                informationList =
                    mandatoryAcknowledgementInfo(
                        text = merchMandatoryAcknowledgement,
                        hasSeen = merchCartStore.hasSeenMerchInformation(MANDATORY_ACKNOWLEDGEMENT_KEY),
                    ),
                merchDocument = merchDocument,
                merchMandatoryAcknowledgement = merchMandatoryAcknowledgement,
                merchTaxStatement = merchTaxStatement,
                canAdd = canAdd,
                cart = cart,
                data = cartQrPayload(cart, conference, versionCode),
            )

        _state.value = ProductsScreenState.Success(state)
    }
}
