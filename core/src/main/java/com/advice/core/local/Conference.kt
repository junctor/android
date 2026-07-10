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
            name = "TEST 2023",
            tagline = "",
            code = "TEST2023",
            homeMenuId = null,
            merchInformation = null,
            maps = ArrayList(),
            kickoffDate = Instant.now(),
            start = Instant.now(),
            end = Instant.now(),
            timezone = "",
            flags = emptyMap(),
            emergencyDocumentId = 123,
            isSelected = false,
            media =
                ConferenceMediaEntry(
                    dark =
                        ConferenceMediaImages(
                            "https://info.defcon.org/blobs/v_aerospace.png",
                            "https://info.defcon.org/blobs/v_aerospace.png",
                            "https://info.defcon.org/blobs/v_aerospace.png",
                        ),
                    light =
                        ConferenceMediaImages(
                            "https://info.defcon.org/blobs/v_aerospace.png",
                            "https://info.defcon.org/blobs/v_aerospace.png",
                            "https://info.defcon.org/blobs/v_aerospace.png",
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
