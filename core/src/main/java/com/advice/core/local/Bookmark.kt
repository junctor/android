package com.advice.core.local

sealed class Bookmark(
    val id: String,
    val value: Boolean
) {
    class SessionBookmark(id: String, value: Boolean) : Bookmark(id, value)
    class ContentBookmark(id: String, value: Boolean) : Bookmark(id, value)
    class TagBookmark(id: String, value: Boolean) : Bookmark(id, value)
}
