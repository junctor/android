package com.advice.data.datasource

import com.advice.core.local.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {

    fun get(): Flow<List<Product>>
}