package com.advice.firebase.data.sources

import com.advice.core.local.Organization
import com.advice.data.session.UserSession
import com.advice.data.sources.OrganizationsDataSource
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.toOrganization
import com.advice.firebase.models.organization.FirebaseOrganization
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseOrganizationDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
) : OrganizationsDataSource {
    private val organizations = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("organizations")
            .snapshotFlow(analytics)
            .map {
                it.toObjectsOrEmpty(FirebaseOrganization::class.java)
                    .sortedBy { it.name }
                    .mapNotNull { it.toOrganization() }
            }
    }.shareIn(
        CoroutineScope(Dispatchers.IO),
        started = SharingStarted.Lazily,
        replay = 1,
    )

    override fun get(): Flow<List<Organization>> {

        return organizations
    }
}
