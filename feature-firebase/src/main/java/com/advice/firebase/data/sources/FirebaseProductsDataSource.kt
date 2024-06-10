package com.advice.firebase.data.sources

import com.advice.core.local.products.Product
import com.advice.data.session.UserSession
import com.advice.data.sources.ProductsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toMerch
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.products.FirebaseProduct
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseProductsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : ProductsDataSource {
    override fun get(): Flow<List<Product>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("products")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseProduct::class.java)
                        .sortedBy { it.sort_order }
                        .mapNotNull { it.toMerch() }
                }
        }
    }
}
