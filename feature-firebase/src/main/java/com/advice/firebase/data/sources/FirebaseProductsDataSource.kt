package com.advice.firebase.data.sources

import com.advice.core.audience.AudiencePolicy
import com.advice.core.local.Conference
import com.advice.core.local.FlowResult
import com.advice.core.local.products.Product
import com.advice.data.session.UserSession
import com.advice.data.sources.ProductsDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.audienceLabel
import com.advice.firebase.extensions.audienceRestriction
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toMerch
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.models.products.FirebaseProduct
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseProductsDataSource(
    private val userSession: UserSession,
    private val tagsDataSource: TagsDataSource,
    private val firestore: FirebaseFirestore,
    private val audiencePolicy: AudiencePolicy,
    applicationScope: CoroutineScope,
) : ProductsDataSource {
    private val products: Flow<FlowResult<List<Product>>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                combine(
                    collectionReference(conference),
                    tagsDataSource.get(),
                    userSession.audienceContext,
                ) { productsResult, tagsResult, context ->
                    when {
                        productsResult is FlowResult.Failure -> productsResult
                        tagsResult is FlowResult.Failure -> tagsResult
                        productsResult is FlowResult.Loading || tagsResult is FlowResult.Loading ->
                            FlowResult.Loading
                        productsResult is FlowResult.Success && tagsResult is FlowResult.Success -> {
                            FlowResult.Success(
                                productsResult.value
                                    .filter {
                                        audiencePolicy.canView(
                                            it.audienceRestriction,
                                            context,
                                            it.audienceLabel,
                                        )
                                    }.mapNotNull { it.toMerch(tagsResult.value) },
                            )
                        }
                        else -> FlowResult.Loading
                    }
                }
            }.shareIn(
                applicationScope,
                started = SharingStarted.Lazily,
                replay = 1,
            )

    private fun collectionReference(conference: Conference): Flow<FlowResult<List<FirebaseProduct>>> =
        firestore
            .collection("conferences")
            .document(conference.code)
            .collection("products")
            .snapshotFlow()
            .closeOnConferenceChange(userSession.getConference())
            .mapSnapshot { querySnapshot ->
                querySnapshot
                    .toObjectsOrEmpty(FirebaseProduct::class.java)
                    .sortedBy { it.sortOrder }
            }

    override fun get(): Flow<FlowResult<List<Product>>> = products
}
