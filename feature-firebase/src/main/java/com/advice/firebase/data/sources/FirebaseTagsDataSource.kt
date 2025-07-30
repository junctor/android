package com.advice.firebase.data.sources

import com.advice.core.local.Bookmark
import com.advice.core.local.TagType
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.snapshotFlowLegacy
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.toTagType
import com.advice.firebase.models.FirebaseTagType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseTagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
) : TagsDataSource {

    private val tagTypes: StateFlow<List<TagType>> =
        userSession.getConference().flatMapMerge { conference ->
            firestore
                .collection("conferences")
                .document(conference.code)
                .collection("tagtypes")
                .snapshotFlowLegacy()
                .closeOnConferenceChange(userSession.getConference())
                .map { querySnapshot ->
                    querySnapshot
                        .toObjectsOrEmpty(FirebaseTagType::class.java)
                        .sortedBy { it.sortOrder }
                        .mapNotNull { it.toTagType() }
                }
                .onStart { emit(emptyList()) }
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.Eagerly,
            initialValue = emptyList(),
        )

    override fun get(): Flow<List<TagType>> =
        combine(tagTypes, bookmarkedEventsDataSource.get()) { tags, bookmarks ->
            val temp = tags
                .toMutableList()
                .sortedBy { it.sortOrder }
                .map {
                    it.copy(
                        tags =
                            it.tags.sortedWith(
                                compareBy(
                                    { it.sortOrder },
                                    { it.label },
                                ),
                            ),
                    )
                }

            // clearing any previous set selections
            temp.flatMap { it.tags }.forEach {
                it.isSelected = false
            }

            for (bookmark in bookmarks.filterIsInstance<Bookmark.TagBookmark>()) {
                temp.flatMap { it.tags }.find { it.id.toString() == bookmark.id }?.isSelected =
                    bookmark.value
            }
            temp
        }
}
