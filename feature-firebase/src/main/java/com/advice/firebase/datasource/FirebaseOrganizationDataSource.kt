package com.advice.firebase.datasource

import com.advice.core.local.Organization
import com.advice.data.UserSession
import com.advice.data.datasource.OrganizationsDataSource
import com.advice.firebase.models.FirebaseOrganization
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toObjectsOrEmpty
import com.advice.firebase.toOrganization
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map

class FirebaseOrganizationDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : OrganizationsDataSource {

    override fun get(): Flow<List<Organization>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("organizations")
                .snapshotFlow()
                .map {
                    it.toObjectsOrEmpty(FirebaseOrganization::class.java)
                        //.filter { !it.hidden || userSession.isDeveloper }
                        .mapNotNull { it.toOrganization() }
                }
        }
    }
}