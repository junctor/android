package com.advice.products.data.repositories

import com.advice.data.session.UserSession
import com.advice.data.sources.ProductsDataSource

class ProductsRepository(
    userSession: UserSession,
    productsDataSource: ProductsDataSource,
) {

    val conference = userSession.getConference()
    val products = productsDataSource.get()
}
