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
            flow { emptyList<FirebaseBookmark>() }
        } else {
            getBookmarks(user)
        }
    }

    override fun clear() {
        TODO("Not yet implemented")
    }

}