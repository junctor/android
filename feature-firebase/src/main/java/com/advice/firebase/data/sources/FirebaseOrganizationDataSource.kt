package com.advice.firebase.data.sources

import com.advice.core.audience.AudiencePolicy
import com.advice.core.local.Organization
import com.advice.data.session.UserSession
import com.advice.data.sources.OrganizationsDataSource
import com.advice.firebase.extensions.SnapshotResult
import com.advice.firebase.extensions.audienceLabel
import com.advice.firebase.extensions.audienceRestriction
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlow
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseOrganizationDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val audiencePolicy: AudiencePolicy,
) : OrganizationsDataSource {
    private val organizations =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                val snapshotFlow =
                    firestore
                        .collection("conferences")
                        .document(conference.code)
                        .collection("organizations")
                        .snapshotFlow()
                        .closeOnConferenceChange(userSession.getConference())

                combine(snapshotFlow, userSession.audienceContext) { snapshotResult, context ->
                    when (snapshotResult) {
                        SnapshotResult.Loading -> emptyList()
                        is SnapshotResult.Failure -> {
                            Timber.e(snapshotResult.error, "Failed to load organizations")
                            emptyList()
                        }

                        is SnapshotResult.Success -> {
                            snapshotResult.snapshot
                                .toObjectsOrEmpty(FirebaseOrganization::class.java)
                                .filter {
                                    audiencePolicy.canView(
                                        it.audienceRestriction,
                                        context,
                                        it.audienceLabel,
                                    )
                                }
                                .mapNotNull { it.toOrganization() }
                                .sortedBy { it.name }
                        }
                    }
                }
            }.shareIn(
                CoroutineScope(Dispatchers.IO),
                started = SharingStarted.Lazily,
                replay = 1,
            )

    override fun get(): Flow<List<Organization>> = organizations

    override suspend fun get(id: Long): Organization? {
        val conference = userSession.currentConference ?: return null

        val snapshot =
            try {
                firestore
                    .document("conferences/${conference.code}/organizations/$id")
                    .get(Source.CACHE)
                    .await()
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to get organization with id: $id")
                return null
            }

        val firebaseOrganization =
            snapshot.toObjectOrNull(FirebaseOrganization::class.java) ?: return null
        if (!audiencePolicy.canView(
                firebaseOrganization.audienceRestriction,
                userSession.currentAudienceContext,
                firebaseOrganization.audienceLabel,
            )
        ) {
            return null
        }
        return firebaseOrganization.toOrganization()
    }
}
