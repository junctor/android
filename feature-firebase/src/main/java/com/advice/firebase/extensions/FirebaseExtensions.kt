package com.advice.firebase.extensions

import androidx.annotation.NonNull
import com.advice.core.local.Action
import com.advice.core.local.Affiliation
import com.advice.core.local.Bookmark
import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.Document
import com.advice.core.local.Event
import com.advice.core.local.FAQ
import com.advice.core.local.Link
import com.advice.core.local.Location
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.NewsArticle
import com.advice.core.local.Organization
import com.advice.core.local.OrganizationLink
import com.advice.core.local.OrganizationLocation
import com.advice.core.local.OrganizationMedia
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.core.local.Speaker
import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.Vendor
import com.advice.firebase.models.FirebaseAction
import com.advice.firebase.models.FirebaseAffiliation
import com.advice.firebase.models.FirebaseArticle
import com.advice.firebase.models.FirebaseBookmark
import com.advice.firebase.models.FirebaseConference
import com.advice.firebase.models.FirebaseDocument
import com.advice.firebase.models.FirebaseEvent
import com.advice.firebase.models.FirebaseFAQ
import com.advice.firebase.models.FirebaseLink
import com.advice.firebase.models.FirebaseLocation
import com.advice.firebase.models.FirebaseMap
import com.advice.firebase.models.FirebaseMenu
import com.advice.firebase.models.FirebaseMenuItem
import com.advice.firebase.models.FirebaseOrganization
import com.advice.firebase.models.FirebaseOrganizationLocation
import com.advice.firebase.models.FirebaseOrganizationMedia
import com.advice.firebase.models.products.FirebaseProduct
import com.advice.firebase.models.products.FirebaseProductMedia
import com.advice.firebase.models.products.FirebaseProductVariant
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.models.FirebaseSpeakerLink
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
            tagline_text,
            code,
            home_menu_id,
            merch_help_doc_id,
            maps.mapNotNull { it.toMap() },
            kickoff_timestamp.toDate().toInstant(),
            start_timestamp.toDate().toInstant(),
            end_timestamp.toDate().toInstant(),
            timezone,
            mapOf(
                "enable_merch" to enable_merch,
                "enable_merch_cart" to enable_merch_cart,
                "enable_wifi" to enable_wifi,
            )
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

fun FirebaseEvent.toEvent(
    tags: List<TagType>,
    speakers: List<Speaker>,
    isBookmarked: Boolean = false
): Event? {
    try {
        val list = tags.flatMap { it.tags.sortedBy { it.sortOrder } }

        val links = links.map { it.toAction() }
        val types = tag_ids.mapNotNull { id ->
            list.find { it.id == id }
        }.sortedBy { list.indexOf(it) }

        val speakers = people
            .map { person ->
                val role = list.find { it.id == person.tag_id }
                val speaker = speakers.find { it.id == person.person_id }
                person to speaker?.copy(roles = listOfNotNull(role))
            }.sortedWith(compareBy({ it.first.sort_order }, { it.second?.name }))
            .mapNotNull { it.second }

        if (types.isEmpty()) {
            return null
        }

        return Event(
            id,
            conference,
            timezone,
            title,
            description,
            begin_timestamp.toDate().toInstant(),
            end_timestamp.toDate().toInstant(),
            updated_timestamp.toDate().toInstant(),
            speakers,
            types,
            location.toLocation()!!,
            links,
            isBookmarked
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
            id = id,
            name = name,
            pronouns = pronouns,
            description = description,
            affiliations = affiliations.mapNotNull { it.toAffiliation() },
            links = links
                .sortedBy { it.sort_order }
                .mapNotNull { it.toLink() },
            roles = emptyList(),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Speaker: ${ex.message}")
        null
    }
}

fun FirebaseAffiliation.toAffiliation(): Affiliation? {
    return try {
        Affiliation(organization, title)
    } catch (ex: Exception) {
        Timber.e("Could not map data to Affiliation: ${ex.message}")
        null
    }
}

fun FirebaseSpeakerLink.toLink(): Link? {
    return try {
        Link(
            title,
            url
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Link: ${ex.message}")
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

fun FirebaseOrganization.toOrganization(): Organization? {
    return try {
        Organization(
            id,
            name,
            description,
            locations.mapNotNull { it.toLocation() },
            links.mapNotNull { it.toLink() },
            media.mapNotNull { it.toMedia() },
            tag_ids,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Organization: ${ex.message}")
        null
    }
}

fun FirebaseOrganizationLocation.toLocation(): OrganizationLocation? {
    return try {
        OrganizationLocation(
            location_id,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to OrganizationLocation: ${ex.message}")
        null
    }
}

fun FirebaseLink.toLink(): OrganizationLink? {
    return try {
        OrganizationLink(
            label,
            type,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Link: ${ex.message}")
        null
    }
}

fun FirebaseOrganizationMedia.toMedia(): OrganizationMedia? {
    return try {
        OrganizationMedia(
            asset_id,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Media: ${ex.message}")
        null
    }
}

fun FirebaseDocument.toDocument(): Document? {
    return try {
        Document(
            id,
            title_text,
            body_text,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Document: ${ex.message}")
        null
    }
}

fun FirebaseArticle.toArticle(): NewsArticle? {
    return try {
        NewsArticle(
            id,
            name,
            text,
            updated_at?.toDate(),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Article: ${ex.message}")
        null
    }
}

fun FirebaseMenu.toMenu(): Menu? {
    return try {
        Menu(
            title_text,
            items.sortedBy { it.sort_order }.mapNotNull { it.toMenuItem() },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Menu: ${ex.message}")
        null
    }
}

fun FirebaseMenuItem.toMenuItem(): MenuItem? {
    return try {
        when (function) {
            "section_heading" -> MenuItem.SectionHeading(
                title_text,
            )

            "divider" -> MenuItem.Divider

            "document" -> MenuItem.Document(
                title_text,
                document ?: error("null document id: $title_text"),
            )

            "schedule" -> {
                if (applied_tag_ids.isEmpty()) error("empty tags: $title_text")
                MenuItem.Schedule(
                    title_text,
                    applied_tag_ids,
                )
            }

            "menu" -> MenuItem.Menu(
                title_text,
                menu_id ?: error("null menu id: $title_text"),
            )

            "people", "locations", "products", "news", "faq" -> MenuItem.Navigation(
                title_text,
                function,
            )

            "organizations" -> MenuItem.Organization(
                title_text,
                applied_tag_ids.first(),
            )

            else -> error("Unknown menu item function: $title_text, $function")
        }
    } catch (ex: Exception) {
        Timber.e("Could not map data to MenuItem: ${ex.message}")
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

fun FirebaseProduct.toMerch(): Product? {
    return try {
        Product(
            id = id,
            label = title,
            baseCost = price_min,
            variants = variants.map { it.toMerchOption(price_min) },
            media = media.map { it.toProductMedia() }
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Merch: ${ex.message}")
        null
    }
}

fun FirebaseProductVariant.toMerchOption(basePrice: Long): ProductVariant {
    return ProductVariant(
        id = variant_id,
        label = title,
        tags = tags,
        extraCost = price - basePrice,
        stockStatus = StockStatus.fromString(stock_status) ?: StockStatus.IN_STOCK,
    )
}

fun FirebaseProductMedia.toProductMedia(): ProductMedia {
    return ProductMedia(
        url = url,
        sortOrder = sort_order,
    )
}
