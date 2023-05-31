package com.advice.firebase.datasource

import com.advice.core.local.TagType
import com.advice.data.UserSession
import com.advice.data.datasource.BookmarkedElementDataSource
import com.advice.data.datasource.TagsDataSource
import com.advice.firebase.models.FirebaseTagType
import com.advice.firebase.snapshotFlow
import com.advice.firebase.toObjectsOrEmpty
import com.advice.firebase.toTagType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map


class FirebaseTagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : TagsDataSource {
    override fun get(): Flow<List<TagType>> {
        return combine(getTagTypes(), bookmarkedEventsDataSource.get()) { tags, bookmarks ->
            val temp = /*listOf(TagType(tags = listOf(Tag.bookmark))) +*/ tags.toMutableList()
                .filter { it.isBrowsable }

            // clearing any previous set selections
            temp.flatMap {
                it.tags
            }.forEach {
                it.isSelected = false
            }

            for (bookmark in bookmarks) {
                temp.flatMap { it.tags }.find { it.id.toString() == bookmark.id }?.isSelected = bookmark.value
            }
            temp
        }
    }

    private fun getTagTypes(): Flow<List<TagType>> {
        return userSession.getConference().flatMapMerge { conference ->
            firestore.collection("conferences")
                .document(conference.code)
                .collection("tagtypes")
                .snapshotFlow()
                .map { querySnapshot ->
                    querySnapshot.toObjectsOrEmpty(FirebaseTagType::class.java)
                        .sortedBy { it.sort_order }
                        .mapNotNull { it.toTagType() }
                }
        }
    }
}


