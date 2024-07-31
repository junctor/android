package com.advice.firebase.extensions

import android.annotation.SuppressLint
import com.advice.core.local.Action
import com.advice.core.local.Affiliation
import com.advice.core.local.Bookmark
import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.Content
import com.advice.core.local.Document
import com.advice.core.local.FAQ
import com.advice.core.local.Link
import com.advice.core.local.Location
import com.advice.core.local.LocationSchedule
import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.MerchInformation
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
import com.advice.core.local.feedback.ContentFeedbackForm
import com.advice.core.local.feedback.FeedbackForm
import com.advice.core.local.feedback.FeedbackItem
import com.advice.core.local.feedback.FeedbackOption
import com.advice.core.local.feedback.FeedbackType
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
import com.advice.firebase.models.feedback.FirebaseFeedbackForm
import com.advice.firebase.models.feedback.FirebaseFeedbackItem
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
            taglineText,
            code,
            homeMenuId,
            MerchInformation(
                merchHelpDocId,
                merchMandatoryAcknowledgement,
                merchTaxStatement,
            ),
            maps.mapNotNull { it.toMap() },
            kickoffTimestamp.toDate().toInstant(),
            startTimestamp.toDate().toInstant(),
            endTimestamp.toDate().toInstant(),
            timezone,
            mapOf(
                "enable_merch" to enableMerch,
                "enable_merch_cart" to enableMerchCart,
                "enable_wifi" to enableWifi,
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
            shortName,
            defaultStatus,
            hierDepth,
            hierExtentLeft,
            hierExtentRight,
            parentId,
            peerSortOrder,
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

fun FirebaseContent.toContents(
    code: String,
    tags: List<TagType>,
    speakers: List<Speaker>,
    bookmarkedEvents: List<Bookmark>,
    locations: List<Location>,
    feedbackforms: List<FeedbackForm>,
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
                    val roles = person.tagIds.mapNotNull { id -> list.find { it.id == id } }
                    val speaker = speakers.find { it.id == person.personId }
                    person to speaker?.copy(roles = roles)
                }.sortedWith(compareBy({ it.first.sortOrder }, { it.second?.name }))
                .mapNotNull { it.second }

        if (types.isEmpty()) {
            Timber.e("Could not find tags for content: $title")
            return null
        }

        val new_sessions = sessions.mapNotNull { session ->
            // if we cannot find the location, ignore this session
            val location = locations.find { it.id == session.locationId }

            if (location == null) {
                Timber.e("Could not find location for session: $title")
                return@mapNotNull null
            }

            val isBookmarked =
                bookmarkedEvents.any { bookmark -> bookmark.id == session.sessionId.toString() }

            Session(
                id = session.sessionId,
                timeZone = session.timezoneName,
                start = session.beginTimestamp.toDate().toInstant(),
                end = session.endTimestamp.toDate().toInstant(),
                location = location,
                isBookmarked = isBookmarked,
            )
        }.sortedBy { it.start }

        val isBookmarked = bookmarkedEvents.any { bookmark -> bookmark.id == id.toString() }

        val feedback = feedbackforms.find { it.id == feedbackFormId }
        val feedbackForm = if (feedback != null) {
            ContentFeedbackForm(
                enable = feedbackEnableTimestamp?.toDate()?.toInstant(),
                disable = feedbackDisableTimestamp?.toDate()?.toInstant(),
                form = feedback,
            )
        } else {
            null
        }

        return Content(
            id = id,
            conference = code,
            title = title,
            description = description,
            updated = updatedTimestamp.toDate().toInstant(),
            speakers = speakers,
            types = types,
            urls = links,
            media = media.mapNotNull { it.toMedia() },
            isBookmarked = isBookmarked,
            sessions = new_sessions,
            feedback = feedbackForm,
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
            colorBackground,
            sortOrder,
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
                .sortedBy { it.sortOrder }
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
            tagIdAsOrganizer,
            tagIds,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Organization: ${ex.message}")
        null
    }

fun FirebaseOrganizationLocation.toLocation(): OrganizationLocation? =
    try {
        OrganizationLocation(
            locationId,
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
            assetId,
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
            titleText,
            bodyText,
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
            updatedAt?.toDate(),
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Article: ${ex.message}")
        null
    }

fun FirebaseMenu.toMenu(): Menu? =
    try {
        Menu(
            id,
            titleText,
            items
                .sortedBy { it.sortOrder }
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
                    titleText,
                )

            "divider" -> MenuItem.Divider

            "document" ->
                MenuItem.Document(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    documentId ?: error("null document id: $titleText"),
                )

            "schedule" -> {
                if (appliedTagIds.isEmpty()) error("empty tags: $titleText")
                MenuItem.Schedule(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    appliedTagIds,
                )
            }

            "menu" ->
                MenuItem.Menu(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    menuId ?: error("null menu id: $titleText"),
                )

            "people", "locations", "products", "news", "faq" ->
                MenuItem.Navigation(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    function,
                )

            "organizations" ->
                MenuItem.Organization(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    appliedTagIds.first(),
                )

            "content" ->
                MenuItem.Content(
                    googleMaterialsymbol,
                    titleText,
                    description,
                )

            else -> error("Unknown menu item function: $titleText, $function")
        }
    } catch (ex: Exception) {
        Timber.e("Could not map data to MenuItem: ${ex.message}")
        null
    }

fun FirebaseMap.toMap(): ConferenceMap? =
    try {
        ConferenceMap(
            nameText,
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
            isBrowsable,
            sortOrder,
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
            baseCost = priceMin,
            variants = variants.mapNotNull { it.toMerchOption(priceMin) },
            media = media.mapNotNull { it.toProductMedia() },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Merch: ${ex.message}")
        null
    }

fun FirebaseProductVariant.toMerchOption(basePrice: Long): ProductVariant? =
    try {
        ProductVariant(
            id = variantId,
            label = title,
            tags = tags,
            extraCost = price - basePrice,
            stockStatus = StockStatus.fromString(stockStatus) ?: StockStatus.IN_STOCK,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to MerchOption: ${ex.message}")
        null
    }

fun FirebaseProductMedia.toProductMedia(): ProductMedia? =
    try {
        ProductMedia(
            url = url,
            sortOrder = sortOrder,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to ProductMedia: ${ex.message}")
        null
    }

fun FirebaseFeedbackForm.toFeedbackForm(): FeedbackForm? =
    try {
        FeedbackForm(
            id = id,
            conference = conferenceId,
            title = nameText,
            items = items.sortedBy { it.sortOrder }.mapNotNull { it.toFeedbackItem() },
            endpoint = submissionUrl,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to FeedbackForm: ${ex.message}")
        null
    }

fun FirebaseFeedbackItem.toFeedbackItem(): FeedbackItem? =
    try {
        val type = when (type) {
            "display_only" -> FeedbackType.DisplayOnly
            "select_one" -> FeedbackType.SelectOne(
                options.sortedBy { it.sortOrder }.map { FeedbackOption(it.id, it.captionText) }
            )

            "multi_select" -> FeedbackType.MultiSelect(
                options.sortedBy { it.sortOrder }.map { FeedbackOption(it.id, it.captionText) }
            )

            "text" -> FeedbackType.TextBox("")
            else -> error("Unknown feedback type: $type")
        }
        FeedbackItem(
            id = id,
            caption = captionText,
            type = type,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to FeedbackItem: ${ex.message}")
        null
    }
