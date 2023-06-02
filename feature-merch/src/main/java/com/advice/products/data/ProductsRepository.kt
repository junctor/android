package com.advice.products.data

import android.content.Context
import com.advice.data.datasource.ProductsDataSource

class ProductsRepository(private val context: Context, private val dataSource: ProductsDataSource) {

    val products = dataSource.get(context)

}