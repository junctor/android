package com.advice.firebase

import androidx.annotation.NonNull
import com.advice.core.local.Action
import com.advice.core.local.Article
import com.advice.core.local.Bookmark
import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.Event
import com.advice.core.local.FAQ
import com.advice.core.local.Location
import com.advice.core.local.Product
import com.advice.core.local.ProductVariant
import com.advice.core.local.ProductMedia
import com.advice.core.local.Speaker
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.Vendor
import com.advice.firebase.models.FirebaseAction
import com.advice.firebase.models.FirebaseArticle
import com.advice.firebase.models.FirebaseBookmark
import com.advice.firebase.models.FirebaseConference
import com.advice.firebase.models.FirebaseEvent
import com.advice.firebase.models.FirebaseFAQ
import com.advice.firebase.models.FirebaseLocation
import com.advice.firebase.models.FirebaseMap
import com.advice.firebase.models.FirebaseMerch
import com.advice.firebase.models.FirebaseProductMedia
import com.advice.firebase.models.FirebaseProductVariant
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.models.FirebaseTag
import com.advice.firebase.models.FirebaseTagType
import com.advice.firebase.models.FirebaseVendor
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
            maps.mapNotNull { it.toMap() },
            kickoff_timestamp.toDate(),
            start_timestamp.toDate(),
            end_timestamp.toDate(),
            timezone
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Conference: ${ex.message}")
        null
    }
}

fun FirebaseLocation.toLocation(children: List<Location> = emptyList()): Location? {
    return try {
        Location(
            id,
            name,
            short_name,
            hotel,
            conference,
            default_status,
            hier_depth,
            hier_extent_left,
            hier_extent_right,
            parent_id,
            peer_sort_order,
            schedule,
            children
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Location: ${ex.message}")
        null
    }
}

fun FirebaseEvent.toEvent(tags: List<TagType>): Event? {
    try {
        val list = tags.flatMap { it.tags.sortedBy { it.sortOrder } }

        val links = links.map { it.toAction() }
        val types = tag_ids.mapNotNull { id ->
            list.find { it.id == id }
        }.sortedBy { list.indexOf(it) }

        return Event(
            id,
            conference,
            title,
            android_description,
            begin_timestamp.toDate(),
            end_timestamp.toDate(),
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

fun FirebaseTag.toTag(): Tag? {
    return try {
        Tag(
            id,
            label,
            description,
            color_background,
            sort_order
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Speaker: ${ex.message}")
        null
    }
}

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

fun FirebaseMap.toMap(): ConferenceMap? {
    return try {
        ConferenceMap(
            name_text,
            filename,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Map: ${ex.message}")
        null
    }
}

fun FirebaseBookmark.toBookmark(): Bookmark? {
    return try {
        Bookmark(
            id, value
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Bookmark: ${ex.message}")
        null
    }
}

fun FirebaseTagType.toTagType(): TagType? {
    return try {
        TagType(
            id, label, category, is_browsable, sort_order, tags.mapNotNull { it.toTag() }
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Bookmark: ${ex.message}")
        null
    }
}

fun FirebaseFAQ.toFAQ() = FAQ(question, answer)

fun FirebaseMerch.toMerch(): Product? {
    return try {
        Product(
            id = id,
            label = title,
            baseCost = (price_min.toFloat() * 100).toLong(), // todo: this is currently a float, fix!
            variants = variants.map { it.toMerchOption() },
            media = media.map { it.toProductMedia() }
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Merch: ${ex.message}")
        null
    }
}

fun FirebaseProductVariant.toMerchOption(): ProductVariant {
    return ProductVariant(
        label = title,
        inStock = stock_status == "IN",
        extraCost = (price.toFloat() * 100).toLong(), // todo: this is currently a float, fix!
    )
}

fun FirebaseProductMedia.toProductMedia(): ProductMedia {
    return ProductMedia(
        url = url,
        sortOrder = sort_order,
    )
}