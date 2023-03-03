package com.advice.firebase.datasource

import com.advice.core.local.Bookmark
import com.advice.core.local.User
import com.advice.data.UserSession
import com.advice.data.datasource.BookmarkedEventsDataSource
import com.advice.firebase.models.FirebaseBookmark
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toBookmark
import com.advice.firebase.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class FirebaseBookmarkedEventsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : BookmarkedEventsDataSource {

    private fun getBookmarks(user: User) =
        userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("users")
                .document(user.id)
                .collection("bookmarks")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseBookmark::class.java).mapNotNull { it.toBookmark() }
                }
        }

    override fun get(): Flow<List<Bookmark>> = userSession.user.flatMapMerge { user ->
        if (user == null) {
            Timber.e("User is null, returning empty bookmarks")
            flow { emit(emptyList()) }
        } else {
            Timber.e("User is NOT null, fetching bookmarks")
            getBookmarks(user)
        }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    override suspend fun bookmark(id: Long, isBookmarked: Boolean) {
        Timber.e("Updating event selection: $id")

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
            .collection("bookmarks")
            .document(id.toString())

        Timber.e("User: ${user.id}, event: ${id}, conference: ${conference.code}")

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
}
