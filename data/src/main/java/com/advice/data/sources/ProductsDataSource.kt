package com.advice.data.sources

import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {
    fun get(): Flow<List<Product>>
    fun getProductVariantsTags(): Flow<List<TagType>>
}
