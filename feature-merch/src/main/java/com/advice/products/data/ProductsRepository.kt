package com.advice.products.data

import com.advice.data.datasource.ProductsDataSource

class ProductsRepository(
    private val dataSource: ProductsDataSource,
) {

    val products = dataSource.get()

}