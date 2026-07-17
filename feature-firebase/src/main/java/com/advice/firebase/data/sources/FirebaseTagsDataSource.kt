package com.advice.firebase.data.sources

import com.advice.core.local.Bookmark
import com.advice.core.local.TagType
import com.advice.data.session.UserSession
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.data.sources.TagsDataSource
import com.advice.firebase.extensions.closeOnConferenceChange
import com.advice.firebase.extensions.mapSnapshot
import com.advice.firebase.extensions.snapshotFlow
import com.advice.firebase.extensions.toObjectsOrEmpty
import com.advice.firebase.extensions.toTagType
import com.advice.firebase.extensions.unwrapList
import com.advice.firebase.models.FirebaseTagType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalCoroutinesApi::class)
class FirebaseTagsDataSource(
    private val userSession: UserSession,
    private val firestore: FirebaseFirestore,
    private val bookmarkedEventsDataSource: BookmarkedElementDataSource,
    private val applicationScope: CoroutineScope,
) : TagsDataSource {
    private val tagTypes: StateFlow<List<TagType>> =
        userSession
            .getConference()
            .flatMapLatest { conference ->
                firestore
                    .collection("conferences")
                    .document(conference.code)
                    .collection("tagtypes")
                    .snapshotFlow()
                    .closeOnConferenceChange(userSession.getConference())
                    .mapSnapshot { querySnapshot ->
                        querySnapshot
                            .toObjectsOrEmpty(FirebaseTagType::class.java)
                            .sortedBy { it.sortOrder }
                            .mapNotNull { it.toTagType() }
                    }
                    .unwrapList("Failed to load tags")
            }.stateIn(
                scope = applicationScope,
                started = SharingStarted.Eagerly,
                initialValue = emptyList(),
            )

    override fun get(): Flow<List<TagType>> =
        combine(tagTypes, bookmarkedEventsDataSource.get()) { tags, bookmarks ->
            val selectedIds =
                bookmarks
                    .filterIsInstance<Bookmark.TagBookmark>()
                    .filter { it.value }
                    .map { it.id }
                    .toSet()

            tags
                .sortedBy { it.sortOrder }
                .map { tagType ->
                    tagType.copy(
                        tags =
                            tagType.tags
                                .sortedWith(
                                    compareBy(
                                        { it.sortOrder },
                                        { it.label },
                                    ),
                                ).map { tag ->
                                    tag.copy(isSelected = tag.id.toString() in selectedIds)
                                },
                    )
                }
        }
}
