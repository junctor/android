package com.advice.firebase.extensions

import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.firebase.models.FirebaseTag
import com.advice.firebase.models.FirebaseTagType
import timber.log.Timber

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
        Timber.e("Could not map data to Tag: ${ex.message}")
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
        Timber.e("Could not map data to TagType: ${ex.message}")
        null
    }
