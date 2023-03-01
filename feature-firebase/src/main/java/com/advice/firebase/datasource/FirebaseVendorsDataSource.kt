package com.advice.firebase.datasource

import com.advice.data.UserSession
import com.advice.data.datasource.VendorsDataSource
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toObjectsOrEmpty
import com.advice.firebase.toVendor
import com.advice.schedule.models.firebase.FirebaseVendor
import com.advice.schedule.models.local.Vendor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseVendorsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : VendorsDataSource {

    override fun get(): Flow<List<Vendor>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("vendors")
                .snapshotFlow()
                .map {
                    it.toObjectsOrEmpty(FirebaseVendor::class.java)
                        .filter { !it.hidden || userSession.isDeveloper }
                        .mapNotNull { it.toVendor() }
                }
        }
    }
}