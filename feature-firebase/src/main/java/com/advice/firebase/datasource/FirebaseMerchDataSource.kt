package com.advice.firebase.datasource

import android.content.Context
import com.advice.core.local.Merch
import com.advice.data.UserSession
import com.advice.data.datasource.MerchDataSource
import com.advice.firebase.models.FirebaseMerch
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toMerch
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseMerchDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : MerchDataSource {

    override fun get(context: Context): Flow<List<Merch>> {
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