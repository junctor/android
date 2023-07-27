package com.advice.core.local

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val id: Long,
    val label: String,
    val description: String,
    val color: String?,
    val sortOrder: Int,
    var isSelected: Boolean = false
) : Parcelable {

    companion object {
        val bookmark = Tag(-1, "Bookmark", "", color = "#000000", -1)
    }

    val isBookmark: Boolean
        get() = this == bookmark
}
