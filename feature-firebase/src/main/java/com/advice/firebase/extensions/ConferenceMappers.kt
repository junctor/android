package com.advice.firebase.extensions

import com.advice.core.local.Conference
import com.advice.core.local.ConferenceMap
import com.advice.core.local.ConferenceMediaEntry
import com.advice.core.local.ConferenceMediaImages
import com.advice.core.local.MerchInformation
import com.advice.firebase.models.FirebaseConference
import com.advice.firebase.models.FirebaseConferenceMediaTheme
import com.advice.firebase.models.FirebaseMap
import timber.log.Timber

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
            emergencyDocumentId,
            media = ConferenceMediaEntry(
                dark = media["dark"]?.toMediaImages(),
                light = media["light"]?.toMediaImages(),
            )
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Conference: ${ex.message}")
        null
    }

fun FirebaseConferenceMediaTheme.toMediaImages(): ConferenceMediaImages =
    ConferenceMediaImages(
        bannerBackground = bannerBackground,
        bannerLogo = bannerLogo,
        squareLogo = squareLogo,
    )

fun FirebaseMap.toMap(): ConferenceMap? =
    try {
        ConferenceMap(
            nameText,
            filename,
            url,
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Map: ${ex.message}")
        null
    }
