package com.advice.firebase.extensions

import android.annotation.SuppressLint
import com.advice.core.local.Action
import com.advice.core.local.Affiliation
import com.advice.core.local.Bookmark
import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.Content
import com.advice.core.local.Document
import com.advice.core.local.Event
import com.advice.core.local.FAQ
import com.advice.core.local.Link
import com.advice.core.local.Location
import com.advice.core.local.LocationSchedule
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.NewsArticle
import com.advice.core.local.Organization
import com.advice.core.local.OrganizationLink
import com.advice.core.local.OrganizationLocation
import com.advice.core.local.OrganizationMedia
import com.advice.core.local.Session
import com.advice.core.local.Speaker
import com.advice.core.local.StockStatus
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.local.Vendor
import com.advice.core.local.products.Product
import com.advice.core.local.products.ProductMedia
import com.advice.core.local.products.ProductVariant
import com.advice.firebase.models.FirebaseAction
import com.advice.firebase.models.FirebaseAffiliation
import com.advice.firebase.models.FirebaseArticle
import com.advice.firebase.models.FirebaseBookmark
import com.advice.firebase.models.FirebaseConference
import com.advice.firebase.models.FirebaseContent
import com.advice.firebase.models.FirebaseDocument
import com.advice.firebase.models.FirebaseFAQ
import com.advice.firebase.models.FirebaseMap
import com.advice.firebase.models.FirebaseMedia
import com.advice.firebase.models.FirebaseSpeaker
import com.advice.firebase.models.FirebaseSpeakerLink
import com.advice.firebase.models.FirebaseTag
import com.advice.firebase.models.FirebaseTagType
import com.advice.firebase.models.FirebaseVendor
import com.advice.firebase.models.location.FirebaseLocation
import com.advice.firebase.models.location.FirebaseLocationSchedule
import com.advice.firebase.models.menu.FirebaseMenu
import com.advice.firebase.models.menu.FirebaseMenuItem
import com.advice.firebase.models.organization.FirebaseLink
import com.advice.firebase.models.organization.FirebaseOrganization
import com.advice.firebase.models.organization.FirebaseOrganizationLocation
import com.advice.firebase.models.products.FirebaseProduct
import com.advice.firebase.models.products.FirebaseProductMedia
import com.advice.firebase.models.products.FirebaseProductVariant
import timber.log.Timber
import java.text.SimpleDateFormat

fun FirebaseConference.toConference(): Conference? =
    try {
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
            ),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Conference: ${ex.message}")
        null
    }

fun FirebaseLocation.toLocation(children: List<Location> = emptyList()): Location? =
    try {
        Location(
            id,
            name,
            short_name,
            default_status,
            hier_depth,
            hier_extent_left,
            hier_extent_right,
            parent_id,
            peer_sort_order,
            schedule?.mapNotNull { it.toSchedule() },
            children,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Location: ${ex.message}")
        null
    }

@SuppressLint("SimpleDateFormat")
fun FirebaseLocationSchedule.toSchedule(): LocationSchedule? =
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        LocationSchedule(
            format.parse(begin).toInstant(),
            format.parse(end).toInstant(),
            notes,
            status,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to LocationSchedule: ${ex.message}")
        null
    }

// todo: implement this method
fun FirebaseContent.toEvents(
    code: String,
    tags: List<TagType>,
    speakers: List<Speaker>,
    bookmarkedEvents: List<Bookmark>,
    locations: List<Location>,
): List<Event>? {
    try {
        Timber.e(">>> toEvents: $title")

        val list = tags.flatMap { it.tags.sortedBy { it.sortOrder } }

        val links = links.map { it.toAction() }
        val types =
            tag_ids
                .mapNotNull { id ->
                    list.find { it.id == id }
                }.sortedBy { list.indexOf(it) }

        val speakers =
            people
                .map { person ->
                    val roles = person.tag_ids.mapNotNull { id -> list.find { it.id == id } }
                    val speaker = speakers.find { it.id == person.person_id }
                    person to speaker?.copy(roles = roles)
                }.sortedWith(compareBy({ it.first.sort_order }, { it.second?.name }))
                .mapNotNull { it.second }

        if (types.isEmpty()) {
            Timber.e("Could not find tags for event: $title")
            return emptyList()
        }

        return sessions.mapNotNull { session ->
            // if we cannot find the location, ignore this session
            val location = locations.find { it.id == session.location_id }

            if (location == null) {
                Timber.e("Could not find location for session: $title")
                return@mapNotNull null
            }
            TODO("implement")
        }
    } catch (ex: Exception) {
        Timber.e("Could not map data to Event: ${ex.message}")
        return null
    }
}

fun FirebaseContent.toContents(
    code: String,
    tags: List<TagType>,
    speakers: List<Speaker>,
    bookmarkedEvents: List<Bookmark>,
    locations: List<Location>,
): Content? {
    try {
        val list = tags.flatMap { it.tags.sortedBy { it.sortOrder } }

        val links = links.map { it.toAction() }
        val types =
            tag_ids
                .mapNotNull { id ->
                    list.find { it.id == id }
                }.sortedBy { list.indexOf(it) }

        val speakers =
            people
                .map { person ->
                    val roles = person.tag_ids.mapNotNull { id -> list.find { it.id == id } }
                    val speaker = speakers.find { it.id == person.person_id }
                    person to speaker?.copy(roles = roles)
                }.sortedWith(compareBy({ it.first.sort_order }, { it.second?.name }))
                .mapNotNull { it.second }

        if (types.isEmpty()) {
            Timber.e("Could not find tags for content: $title")
            return null
        }

        val new_sessions = sessions.mapNotNull { session ->
            // if we cannot find the location, ignore this session
            val location = locations.find { it.id == session.location_id }

            if (location == null) {
                Timber.e("Could not find location for session: $title")
                return@mapNotNull null
            }

            val isBookmarked =
                bookmarkedEvents.any { bookmark -> bookmark.id == session.session_id.toString() }

            Session(
                id = session.session_id,
                timeZone = session.timezone_name,
                start = session.begin_timestamp.toDate().toInstant(),
                end = session.end_timestamp.toDate().toInstant(),
                location = location,
                isBookmarked = isBookmarked,
            )
        }.sortedBy { it.start }

        val isBookmarked = bookmarkedEvents.any { bookmark -> bookmark.id == id.toString() }

        return Content(
            id = id,
            conference = code,
            title = title,
            description = description,
            updated = updated_timestamp.toDate().toInstant(),
            speakers = speakers,
            types = types,
            urls = links,
            isBookmarked = isBookmarked,
            sessions = new_sessions,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Content: ${ex.message}")
        return null
    }
}

private fun FirebaseAction.toAction() = Action(this.label, this.url)

fun FirebaseTag.toTag(): Tag? =
    try {
        Tag(
            id,
            label,
            description,
            color_background,
            sort_order,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Speaker: ${ex.message}")
        null
    }

fun FirebaseSpeaker.toSpeaker(): Speaker? =
    try {
        Speaker(
            id = id,
            name = name,
            pronouns = pronouns,
            description = description,
            affiliations = affiliations.mapNotNull { it.toAffiliation() },
            links =
            links
                .sortedBy { it.sort_order }
                .mapNotNull { it.toLink() },
            roles = emptyList(),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Speaker: ${ex.message}")
        null
    }

fun FirebaseAffiliation.toAffiliation(): Affiliation? =
    try {
        Affiliation(organization, title)
    } catch (ex: Exception) {
        Timber.e("Could not map data to Affiliation: ${ex.message}")
        null
    }

fun FirebaseSpeakerLink.toLink(): Link? =
    try {
        Link(
            title,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Link: ${ex.message}")
        null
    }

fun FirebaseVendor.toVendor(): Vendor? =
    try {
        Vendor(
            id,
            name,
            description,
            link,
            partner,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Vendor: ${ex.message}")
        null
    }

fun FirebaseOrganization.toOrganization(): Organization? =
    try {
        Organization(
            id,
            name,
            description,
            locations.mapNotNull { it.toLocation() },
            links.mapNotNull { it.toLink() },
            media.mapNotNull { it.toMedia() },
            tag_id_as_organizer,
            tag_ids,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Organization: ${ex.message}")
        null
    }

fun FirebaseOrganizationLocation.toLocation(): OrganizationLocation? =
    try {
        OrganizationLocation(
            location_id,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to OrganizationLocation: ${ex.message}")
        null
    }

fun FirebaseLink.toLink(): OrganizationLink? =
    try {
        OrganizationLink(
            label,
            type,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Link: ${ex.message}")
        null
    }

fun FirebaseMedia.toMedia(): OrganizationMedia? =
    try {
        OrganizationMedia(
            asset_id,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Media: ${ex.message}")
        null
    }

fun FirebaseDocument.toDocument(): Document? =
    try {
        Document(
            id,
            title_text,
            body_text,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Document: ${ex.message}")
        null
    }

fun FirebaseArticle.toArticle(): NewsArticle? =
    try {
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

fun FirebaseMenu.toMenu(): Menu? =
    try {
        Menu(
            id,
            title_text,
            items
                .sortedBy { it.sort_order }
                .mapNotNull { it.toMenuItem() },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Menu: ${ex.message}")
        null
    }

fun FirebaseMenuItem.toMenuItem(): MenuItem? =
    try {
        when (function) {
            "section_heading" ->
                MenuItem.SectionHeading(
                    title_text,
                )

            "divider" -> MenuItem.Divider

            "document" ->
                MenuItem.Document(
                    google_materialsymbol,
                    title_text,
                    description,
                    document_id ?: error("null document id: $title_text"),
                )

            "schedule" -> {
                if (applied_tag_ids.isEmpty()) error("empty tags: $title_text")
                MenuItem.Schedule(
                    google_materialsymbol,
                    title_text,
                    description,
                    applied_tag_ids,
                )
            }

            "menu" ->
                MenuItem.Menu(
                    google_materialsymbol,
                    title_text,
                    description,
                    menu_id ?: error("null menu id: $title_text"),
                )

            "people", "locations", "products", "news", "faq" ->
                MenuItem.Navigation(
                    google_materialsymbol,
                    title_text,
                    description,
                    function,
                )

            "organizations" ->
                MenuItem.Organization(
                    google_materialsymbol,
                    title_text,
                    description,
                    applied_tag_ids.first(),
                )

            "content" ->
                MenuItem.Content(
                    google_materialsymbol,
                    title_text,
                    description,
                )

            else -> error("Unknown menu item function: $title_text, $function")
        }
    } catch (ex: Exception) {
        Timber.e("Could not map data to MenuItem: ${ex.message}")
        null
    }

fun FirebaseMap.toMap(): ConferenceMap? =
    try {
        ConferenceMap(
            name_text,
            filename,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Map: ${ex.message}")
        null
    }

fun FirebaseBookmark.toBookmark(): Bookmark? =
    try {
        Bookmark(
            id,
            value,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Bookmark: ${ex.message}")
        null
    }

fun FirebaseTagType.toTagType(): TagType? =
    try {
        TagType(
            id,
            label,
            category,
            is_browsable,
            sort_order,
            tags.mapNotNull { it.toTag() },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Bookmark: ${ex.message}")
        null
    }

fun FirebaseFAQ.toFAQ() = FAQ(question, answer)

fun FirebaseProduct.toMerch(): Product? =
    try {
        Product(
            id = id,
            label = title,
            baseCost = price_min,
            variants = variants.mapNotNull { it.toMerchOption(price_min) },
            media = media.mapNotNull { it.toProductMedia() },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Merch: ${ex.message}")
        null
    }

fun FirebaseProductVariant.toMerchOption(basePrice: Long): ProductVariant? =
    try {
        ProductVariant(
            id = variant_id,
            label = title,
            tags = tags,
            extraCost = price - basePrice,
            stockStatus = StockStatus.fromString(stock_status) ?: StockStatus.IN_STOCK,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to MerchOption: ${ex.message}")
        null
    }

fun FirebaseProductMedia.toProductMedia(): ProductMedia? =
    try {
        ProductMedia(
            url = url,
            sortOrder = sort_order,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to ProductMedia: ${ex.message}")
        null
    }
