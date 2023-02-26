package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseBookmark
import com.advice.core.local.Conference
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import timber.log.Timber

interface TagsDataSource : DataSource<FirebaseTagType>

interface BookmarkedElementDataSource {

    fun get(): Flow<List<FirebaseBookmark>>

    suspend fun clear()

    suspend fun bookmark(id: Long, isBookmarked: Boolean)
}

class FirebaseTagsDataSourceImpl(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : TagsDataSource {
    override fun get(): Flow<List<FirebaseTagType>> {
        return combine(getTagTypes(), bookmarkedEventsDataSource.get()) { tags, bookmarks ->
            // clearing any previous set selections
            tags.flatMap {
                it.tags
            }.forEach {
                it.isSelected = false
            }

            Timber.e("Bookmarks: $bookmarks")
            for (bookmark in bookmarks) {
                tags.flatMap { it.tags }.find { it.id.toString() == bookmark.id }?.isSelected = bookmark.value
            }
            tags
        }
    }

    override suspend fun clear() {
        TODO("Not yet implemented")
    }

    private fun getTagTypes(): Flow<List<FirebaseTagType>> {
        return userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection("tagtypes")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseTagType::class.java)
                        .sortedBy { it.sort_order }
                }
        }
    }

    data class MyObject(val conference: Conference, val user: FirebaseUser?)
}


class FirebaseBookmarkedTagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : BookmarkedElementDataSource {
    override fun get(): Flow<List<FirebaseBookmark>> {
        return combine(userSession.user, userSession.conference) { user, conference ->
            FirebaseTagsDataSourceImpl.MyObject(conference, user)
        }.flatMapMerge {
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(it.conference.code)
                .collection("users")
                .document(it.user!!.uid)
                .collection("types")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseBookmark::class.java)
                }
        }
    }

    override suspend fun bookmark(id: Long, isBookmarked: Boolean) {
        Timber.e("Updating type selection: $id")

        val conference = userSession.currentConference
        val user = userSession.currentUser

        if (user == null) {
            Timber.e("User is null!")
            return
        }

        val document = firestore.collection(DatabaseManager.CONFERENCES)
            .document(conference.code)
            .collection(DatabaseManager.USERS)
            .document(user.uid)
            .collection(DatabaseManager.TYPES)
            .document(id.toString())

        Timber.e("User: ${user.uid}, type: ${id}, conference: ${conference.code}")

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
        val conference = userSession.currentConference
        val user = userSession.currentUser

        if (user == null) {
            Timber.e("User is null!")
            return
        }

        firestore.collection(DatabaseManager.CONFERENCES)
            .document(conference.code)
            .collection(DatabaseManager.USERS)
            .document(user.uid)
            .collection(DatabaseManager.TYPES)
            .get()
            .addOnCompleteListener {
                for (document in it.result.documents) {
                    document.reference.delete()
                }
            }
    }
}
