package com.advice.firebase.data.sources

import com.advice.core.local.Organization
import com.advice.data.session.UserSession
import com.advice.data.sources.OrganizationsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toObjectOrNull
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.toOrganization
import com.advice.firebase.models.organization.FirebaseOrganization
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseOrganizationDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
) : OrganizationsDataSource {
    private val organizations = userSession.getConference().flatMapMerge { conference ->
        firestore.collection("conferences")
            .document(conference.code)
            .collection("organizations")
            .snapshotFlowLegacy()
            .closeOnConferenceChange(userSession.getConference())
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

    override suspend fun get(id: Long): Organization? {
        val conference = userSession.currentConference ?: return null

        val snapshot = try {
            firestore.document("conferences/${conference.code}/organizations/$id")
                .get(Source.CACHE)
                .await()
        } catch (ex: Exception) {
            Timber.e(ex, "Failed to get organization with id: $id")
            return null
        }

        return snapshot.toObjectOrNull(FirebaseOrganization::class.java)?.toOrganization()
    }
}
