package com.advice.products.presentation.state

import com.advice.core.local.products.Product
import com.advice.products.ui.components.DismissibleInformation

data class ProductsState(
    val products: List<Product>,
    val informationList: List<DismissibleInformation>,
    val merchDocument: Long? = null,
    val merchMandatoryAcknowledgement: String? = null,
    val merchTaxStatement: String? = null,
    val canAdd: Boolean = false,
    val cart: List<Product> = emptyList(),
    val json: String? = null,
)
