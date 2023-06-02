package com.advice.firebase.datasource

import android.content.Context
import com.advice.core.local.Product
import com.advice.data.UserSession
import com.advice.data.datasource.ProductsDataSource
import com.advice.firebase.models.FirebaseMerch
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toMerch
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseProductsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : ProductsDataSource {

    override fun get(context: Context): Flow<List<Product>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("products")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseMerch::class.java)
                        .sortedBy { it.sort_order }
                        .mapNotNull { it.toMerch() }
                }
        }
    }
}