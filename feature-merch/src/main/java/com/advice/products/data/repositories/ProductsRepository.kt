package com.advice.products.data.repositories

import com.advice.data.sources.ProductsDataSource

class ProductsRepository(
    private val dataSource: ProductsDataSource,
) {

    val products = dataSource.get()

}