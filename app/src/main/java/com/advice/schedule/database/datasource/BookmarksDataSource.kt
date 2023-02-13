package com.advice.schedule.database.datasource

import com.advice.core.firebase.FirebaseBookmark
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber

class BookmarksDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<FirebaseBookmark> {

    private fun getBookmarks(user: FirebaseUser) =
        userSession.conference.flatMapMerge { conference ->
            firestore.collection(DatabaseManager.CONFERENCES)
                .document(conference.code)
                .collection(DatabaseManager.USERS)
                .document(user.uid)
                .collection(DatabaseManager.BOOKMARKS)
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseBookmark::class.java)
                }
        }

    override fun get(): Flow<List<FirebaseBookmark>> = userSession.user.flatMapMerge { user ->
        if (user == null) {
            Timber.e("User is null, returning empty bookmarks")
            flow { emit(emptyList()) }
        } else {
            Timber.e("User is NOT null, fetching bookmarks")
            getBookmarks(user)
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}