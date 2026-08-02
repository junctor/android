package com.advice.products.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.advice.core.local.Conference
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.core.local.products.ProductVariantSelection
import com.advice.core.storage.MerchCartStore
import com.advice.products.data.repositories.ProductsRepository
import com.advice.products.presentation.state.ProductsScreenState
import com.advice.products.presentation.state.ProductsState
import com.advice.products.ui.components.DismissibleInformation
import com.advice.products.utils.toStringData
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class ProductsViewModel(
    private val versionCode: Int,
) : ViewModel(),
    KoinComponent {
    private val repository by inject<ProductsRepository>()
    private val merchCartStore by inject<MerchCartStore>()
    private val cart by inject<ProductCart>()

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
                        }
                }
                launch {
                    repository.products
                        .catch { emitError("products", it) }
                        .collect {
                            if (hasLoadError) return@collect
                            products.clear()
                            products.addAll(it.sortedByDescending { product -> product.inStock })
                            updateSummary()
                        }
                }
                launch {
                    repository.variants
                        .catch { emitError("variants", it) }
                        .collect {
                            if (hasLoadError) return@collect
                            productVariantTags.clear()
                            productVariantTags.addAll(it)
                            if (products.isNotEmpty()) {
                                updateSummary()
                            }
                        }
                }
            }
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
            val pruned = MerchCartStore.pruneSelectionsToCatalog(selections, products)
            if (pruned != selections) {
                cart.clear()
                pruned.forEach { cart.add(it) }
                selections = pruned
            }
            merchCartStore.setSelectedProducts(conferenceId, selections)
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
        viewModelScope.launch {
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
        selections: List<ProductVariantSelection> = this.cart.getSelections(),
    ) {
        val filter = productVariantTags.flatMap { it.tags }.filter { it.isSelected }

        val filteredProducts: List<Product> = getFilteredProducts(products, filter)

        val cart =
            selections.mapNotNull { selection ->
                val product = products.find { it.id == selection.id } ?: return@mapNotNull null
                val variant =
                    product.variants.find { it.id == selection.variant } ?: return@mapNotNull null
                return@mapNotNull ProductSelection(product, variant, selection.quantity)
            }

        if (hasLoadError) return

        val state =
            ProductsState(
                groups = groupProducts(filteredProducts),
                productVariantTagTypes = productVariantTags,
                informationList = getInformationList(),
                merchDocument = merchDocument,
                merchMandatoryAcknowledgement = merchMandatoryAcknowledgement,
                merchTaxStatement = merchTaxStatement,
                canAdd = canAdd,
                cart = cart,
                data = cart.toStringData(conference = conference, versionCode = versionCode),
            )

        _state.tryEmit(ProductsScreenState.Success(state))
    }

    private fun getInformationList(): MutableList<DismissibleInformation> {
        val list = mutableListOf<DismissibleInformation>()
        // Legal information about sales being cash only and include Nevada State Sales Tax
        val text = merchMandatoryAcknowledgement
        if (!merchCartStore.hasSeenMerchInformation("mandatory_acknowledgement") && text != null) {
            list.add(
                DismissibleInformation(
                    key = "mandatory_acknowledgement",
                    text = text,
                    document = null,
                ),
            )
        }
        return list
    }
}
