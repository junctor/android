package com.advice.firebase.data.sources

import com.advice.core.local.Conference
import com.advice.core.local.TagType
import com.advice.core.local.products.Product
import com.advice.data.session.UserSession
import com.advice.data.sources.ProductsDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toMerch
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.products.FirebaseProduct
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseProductsDataSource(
    private val userSession: UserSession,
    private val tagsDataSource: TagsDataSource,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : ProductsDataSource {

    private val products: Flow<List<Product>> =
        userSession.getConference().flatMapMerge { conference ->
            combine(collectionReference(conference), tagsDataSource.get()) { products, tags ->
                products.mapNotNull { it.toMerch(tags) }
            }
        }.shareIn(
            CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private val variants: Flow<List<TagType>> =
        tagsDataSource.get().map { tags ->
            val variants = tags.find { it.category == "merch-variant" } ?: return@map emptyList()
            listOf(variants)
        }.shareIn(
            CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Lazily,
            replay = 1,
        )

    private fun collectionReference(conference: Conference): Flow<List<FirebaseProduct>> =
        firestore.collection("conferences")
            .document(conference.code)
            .collection("products")
            .snapshotFlow(analytics)
            .map { querySnapshot ->
                querySnapshot.toObjectsOrEmpty(FirebaseProduct::class.java)
                    .sortedBy { it.sortOrder }
            }

    override fun get(): Flow<List<Product>> {
        return products
    }

    override fun getProductVariantsTags(): Flow<List<TagType>> {
        return variants
    }
}
