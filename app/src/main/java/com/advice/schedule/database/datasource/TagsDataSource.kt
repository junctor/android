package com.advice.schedule.database.datasource

import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.database.UserSession
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.toObjectsOrEmpty
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import timber.log.Timber

class TagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore
) : DataSource<FirebaseTagType> {
    override fun get(): Flow<List<FirebaseTagType>> {
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

    suspend fun updateTypeIsSelected(type: FirebaseTag) {
        Timber.e("Updating type selection: $type")

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
            .document(type.id.toString())

        Timber.e("User: ${user.uid}, type: ${type.id}, conference: ${conference.code}")

        if (!type.isSelected) {
            Timber.e("Adding item")
            document.set(
                mapOf(
                    "id" to type.id.toString(),
                    "value" to true
                )
            )
        } else {
            Timber.e("Deleting item")
            document.delete()
        }

    }

    override fun clear() {
        TODO("Not yet implemented")
    }
}