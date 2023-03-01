package com.advice.firebase

import androidx.annotation.NonNull
import com.advice.core.firebase.FirebaseAction
import com.advice.core.firebase.FirebaseArticle
import com.advice.core.firebase.FirebaseConference
import com.advice.core.firebase.FirebaseEvent
import com.advice.core.local.Conference
import com.advice.core.local.FAQ
import com.advice.schedule.models.firebase.FirebaseFAQ
import com.advice.schedule.models.firebase.FirebaseLocation
import com.advice.schedule.models.firebase.FirebaseSpeaker
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.models.firebase.FirebaseType
import com.advice.schedule.models.firebase.FirebaseVendor
import com.advice.schedule.models.local.Action
import com.advice.schedule.models.local.Article
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion
import com.advice.schedule.models.local.Location
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.models.local.Type
import com.advice.schedule.models.local.Vendor
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber


fun <T> QuerySnapshot.toObjectsOrEmpty(@NonNull clazz: Class<T>): List<T> {
    return try {
        toObjects(clazz)
    } catch (ex: Exception) {
        Timber.e("Could not map data to objects: ${ex.message}")
        return emptyList()
    }
}

fun <T> DocumentSnapshot.toObjectOrNull(@NonNull clazz: Class<T>): T? {
    return try {
        toObject(clazz)
    } catch (ex: Exception) {
        Timber.e("Could not map data to objects: ${ex.message}")
        return null
    }
}

fun Query.snapshotFlow(): Flow<QuerySnapshot> = callbackFlow {
    val listenerRegistration = addSnapshotListener { value, error ->
        if (error != null) {
            close()
            return@addSnapshotListener
        }
        if (value != null)
            trySend(value)
    }
    awaitClose {
        listenerRegistration.remove()
    }
}
fun FirebaseConference.toConference(): Conference? {
    return try {
        Conference(
            id,
            name,
            description,
            codeofconduct,
            supportdoc,
            code,
            maps,
            start_timestamp.toDate(),
            end_timestamp.toDate(),
            timezone
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Conference: ${ex.message}")
        null
    }
}

fun FirebaseType.toType(): Type? {
    return try {
        val actions = ArrayList<Action>()
        discord_url?.let {
            if (it.isNotBlank()) {
                actions.add(Action(Action.getLabel(it), it))
            }
        }

        subforum_url?.let {
            if (it.isNotBlank()) {
                actions.add(Action(Action.getLabel(it), it))
            }
        }

        Type(
            id,
            name,
            conference,
            color,
            description,
            actions
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Location: ${ex.message}")
        null
    }
}

fun FirebaseLocation.toLocation(): Location? {
    return try {
        Location(
            id,
            name,
            short_name,
            hotel,
            conference,
            default_status, hier_depth, hier_extent_left, hier_extent_right, parent_id, peer_sort_order, schedule
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Location: ${ex.message}")
        null
    }
}

fun FirebaseEvent.toEvent(tags: List<FirebaseTagType>): Event? {
    try {
        val list = tags.flatMap { it.tags.sortedBy { it.sort_order } }

        val links = links.map { it.toAction() }
        val types = tag_ids.mapNotNull { id ->
            list.find { it.id == id }
        }.sortedBy { list.indexOf(it) }

        return Event(
            id,
            conference,
            title,
            android_description,
            begin_timestamp,
            end_timestamp,
            //todo:
            updated_timestamp.seconds.toString(),
            speakers.mapNotNull { it.toSpeaker() },
            types,
            location.toLocation()!!,
            links
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Event: ${ex.message}")
        return null
    }
}
private fun FirebaseAction.toAction() =
    Action(this.label, this.url)

fun FirebaseSpeaker.toSpeaker(): Speaker? {
    return try {
        Speaker(
            id,
            name,
            description,
            link,
            twitter,
            title
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Speaker: ${ex.message}")
        null
    }
}

fun FirebaseVendor.toVendor(): Vendor? {
    return try {
        Vendor(
            id,
            name,
            description,
            link,
            partner
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Vendor: ${ex.message}")
        null
    }
}

fun FirebaseArticle.toArticle(): Article? {
    return try {
        Article(
            id,
            name,
            text
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Article: ${ex.message}")
        null
    }
}

fun FirebaseFAQ.toFAQ(isExpanded: Boolean = false): Pair<FAQQuestion, FAQAnswer>? {
    return try {
        FAQQuestion(id, question, isExpanded) to FAQAnswer(id, answer, isExpanded)
    } catch (ex: Exception) {
        Timber.e("Could not map data to FAQ: ${ex.message}")
        null
    }
}

fun FirebaseFAQ.toFAQ() = FAQ(question, answer)