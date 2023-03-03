package com.advice.firebase.datasource

import com.advice.core.local.Bookmark
import com.advice.data.UserSession
import com.advice.data.datasource.BookmarkedElementDataSource
import com.advice.firebase.CurrentUserState
import com.advice.firebase.models.FirebaseBookmark
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toBookmark
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FirebaseBookmarkedTagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : BookmarkedElementDataSource {
    override fun get(): Flow<List<Bookmark>> {
        return combine(userSession.user, userSession.getConference()) { user, conference ->
            CurrentUserState(conference, user)
        }.flatMapMerge {
            firestore.collection("conferences")
                .document(it.conference.code)
                .collection("users")
                .document(it.user!!.id)
                .collection("types")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseBookmark::class.java).mapNotNull { it.toBookmark() }
                }
        }
    }

    override suspend fun bookmark(id: Long, isBookmarked: Boolean) {
        Timber.e("Updating type selection: $id")

        val conference = userSession.currentConference!!
        val user = userSession.currentUser

        if (user == null) {
            Timber.e("User is null!")
            return
        }

        val document = firestore.collection("conferences")
            .document(conference.code)
            .collection("users")
            .document(user.id)
            .collection("types")
            .document(id.toString())

        Timber.e("User: ${user.id}, type: ${id}, conference: ${conference.code}")

        if (isBookmarked) {
            Timber.e("Adding item")
            document.set(
                mapOf(
                    "id" to id.toString(),
                    "value" to true
                )
            )
        } else {
            Timber.e("Deleting item")
            document.delete()
        }
    }


    override suspend fun clear() {
        val conference = userSession.currentConference!!
        val user = userSession.currentUser

        if (user == null) {
            Timber.e("User is null!")
            return
        }

        firestore.collection("conferences")
            .document(conference.code)
            .collection("users")
            .document(user.id)
            .collection("types")
            .get()
            .addOnCompleteListener {
                for (document in it.result.documents) {
                    document.reference.delete()
                }
            }
    }
}