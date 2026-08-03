package com.advice.merch.presentation.state

import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductSelection
import com.advice.merch.ui.components.DismissibleInformation

sealed class ProductsScreenState {
    data object Loading : ProductsScreenState()

    data object Error : ProductsScreenState()

    data class Success(
        val data: ProductsState,
    ) : ProductsScreenState()
}

data class ProductsState(
    val groups: Map<Tag, List<Product>>,
    val productVariantTagTypes: List<TagType>,
    val informationList: List<DismissibleInformation>,
    val merchDocument: Long? = null,
    val merchMandatoryAcknowledgement: String? = null,
    val merchTaxStatement: String? = null,
    val canAdd: Boolean = false,
    val cart: List<ProductSelection> = emptyList(),
    val data: String? = null,
) {
    val products = groups.values.flatten()
}
