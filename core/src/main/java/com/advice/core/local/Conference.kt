package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.Instant

@Parcelize
data class Conference(
    val id: Long,
    val name: String,
    val tagline: String?,
    val code: String,
    val homeMenuId: Long?,
    val merchInformation: MerchInformation?,
    val maps: List<ConferenceMap>,
    val kickoffDate: Instant,
    val start: Instant,
    val end: Instant,
    val timezone: String,
    val flags: Map<String, Boolean>,
    val emergencyDocumentId: Long?,
    var isSelected: Boolean = false,
    val media: ConferenceMediaEntry? = null,
) : Parcelable {

    val hasFinished: Boolean
        get() = end.compareTo(Instant.now()) == -1

    fun squareLogo(darkTheme: Boolean): String? {
        return media?.let {
            if (darkTheme) {
                it.dark?.squareLogo
            } else {
                it.light?.squareLogo
            }
        }
    }

    companion object {
        val Zero = Conference(
            id = -1,
            name = "TEST CON 2026",
            tagline = "Welcome to TEST CON - largest conference in the world",
            code = "TEST2026",
            homeMenuId = null,
            merchInformation = null,
            maps = ArrayList(),
            kickoffDate = Instant.now(),
            start = Instant.now(),
            end = Instant.now(),
            timezone = "America/Los_Angeles",
            flags = emptyMap(),
            emergencyDocumentId = 123,
            isSelected = false,
            media =
                ConferenceMediaEntry(
                    dark =
                        ConferenceMediaImages(
                            null,
                            null,
                            "https://info.defcon.org/blobs/dc34/dc-34-logo.webp",
                        ),
                    light =
                        ConferenceMediaImages(
                            null,
                            null,
                            "https://info.defcon.org/blobs/dc34/dc-34-logo.webp",
                        ),
                ),
        )
    }
}

@Parcelize
data class ConferenceMediaEntry(
    val dark: ConferenceMediaImages?,
    val light: ConferenceMediaImages?,
) : Parcelable

@Parcelize
data class ConferenceMediaImages(
    val bannerBackground: String?,
    val bannerLogo: String?,
    val squareLogo: String?
) : Parcelable
