package com.advice.data.datasource

import android.content.Context
import com.advice.core.local.Product
import kotlinx.coroutines.flow.Flow

interface ProductsDataSource {

    fun get(context: Context): Flow<List<Product>>
}