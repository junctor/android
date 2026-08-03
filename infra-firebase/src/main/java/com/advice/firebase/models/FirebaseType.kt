package com.advice.firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class FirebaseType(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: Long = -1,
    @get:PropertyName("name")
    @set:PropertyName("name")
    var name: String = "",
    @get:PropertyName("description")
    @set:PropertyName("description")
    var description: String = "",
    @get:PropertyName("conference")
    @set:PropertyName("conference")
    var conference: String = "",
    @get:PropertyName("color")
    @set:PropertyName("color")
    var color: String = "#343434",
    @get:PropertyName("discord_url")
    @set:PropertyName("discord_url")
    var discordUrl: String? = null,
    @get:PropertyName("subforum_url")
    @set:PropertyName("subforum_url")
    var subforumUrl: String? = null,
    @get:PropertyName("youtube_url")
    @set:PropertyName("youtube_url")
    var youtubeUrl: String? = null,
    @get:PropertyName("eventdescriptionfooter")
    @set:PropertyName("eventdescriptionfooter")
    var eventdescriptionfooter: String? = null,
    @get:PropertyName("updated_at")
    @set:PropertyName("updated_at")
    var updatedAt: String? = null,
    @get:PropertyName("tags")
    @set:PropertyName("tags")
    var tags: String? = null,
) : Parcelable
