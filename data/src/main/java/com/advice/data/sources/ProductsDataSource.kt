package com.advice.data.sources

import com.advice.core.local.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {

    fun get(): Flow<List<Product>>
}
