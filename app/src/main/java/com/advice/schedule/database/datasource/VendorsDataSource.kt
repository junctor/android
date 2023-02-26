package com.advice.schedule.database.datasource

import com.advice.schedule.App
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseVendor
import com.advice.schedule.models.local.Vendor
import com.advice.schedule.toObjectsOrEmpty
import com.advice.schedule.toVendor
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class VendorsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<Vendor> {
    override fun get(): Flow<List<Vendor>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection(DatabaseManager.VENDORS)
                .snapshotFlow()
                .map {
                    it.toObjectsOrEmpty(FirebaseVendor::class.java)
                        .filter { !it.hidden || App.isDeveloper }
                        .mapNotNull { it.toVendor() }
                }
        }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }
}