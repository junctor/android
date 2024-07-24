package com.advice.firebase.models.organization

import android.os.Parcelable
import com.advice.firebase.models.FirebaseMedia
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseOrganization(
    @get:PropertyName("conference_id")
    @set:PropertyName("conference_id")
    var conferenceId: Long = -1L,
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1L,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String? = null,
    @get:PropertyName("links")
    @set:PropertyName("links")
    var links: List<FirebaseLink> = emptyList(),
    @get:PropertyName("locations")
    @set:PropertyName("locations")
    var locations: List<FirebaseOrganizationLocation> = emptyList(),
    @get:PropertyName("media")
    @set:PropertyName("media")
    var media: List<FirebaseMedia> = emptyList(),
    @get:PropertyName("tag_id_as_organizer")
    @set:PropertyName("tag_id_as_organizer")
    var tagIdAsOrganizer: Long? = null,
    @get:PropertyName("tag_ids")
    @set:PropertyName("tag_ids")
    var tagIds: List<Long> = emptyList(),
) : Parcelable
