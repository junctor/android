package com.advice.data

import android.content.Context
import android.content.SharedPreferences
import com.advice.core.local.Bookmark
import com.advice.core.local.Content
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.data.sources.BookmarkedElementDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import timber.log.Timber

class SharedPreferencesBookmarkDataSource(
    private val context: Context,
) : BookmarkedElementDataSource {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    init {
        /**
         * Backwards compatibility for events. If the key does not contain a colon, it's an event,
         * and we'll update it to the new key format.
         */
        if (!prefs.getBoolean(HAS_MIGRATED_KEY, false)) {
            Timber.i("Updating legacy bookmarks.")
            prefs.all.forEach {
                val isValid = it.key.contains(":")
                if (!isValid) {
                    prefs.edit {
                        remove(it.key)
                        putBoolean("session:${it.key}", true)
                    }
                }
            }
            prefs.edit {
                putBoolean(HAS_MIGRATED_KEY, true)
            }
        }
    }

    override fun get(): Flow<List<Bookmark>> =
        callbackFlow {
            trySend(getBookmarks()).isSuccess

            val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
                trySend(getBookmarks()).isSuccess
            }

            prefs.registerOnSharedPreferenceChangeListener(listener)

            awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
        }

    private fun getBookmarks(): List<Bookmark> {
        return prefs.all.map { it.toBookmark() }
    }

    // todo: this could be a lot cleaner.
    private fun Map.Entry<String, Any?>.toBookmark(): Bookmark {
        val key = key.replace("session:", "")
            .replace("content:", "")
            .replace("tag:", "")
        return Bookmark(key, value as Boolean)
    }

    override suspend fun bookmark(content: Content, isBookmarked: Boolean) {
        bookmark(id = "content:${content.id}", isBookmarked)
    }

    override suspend fun bookmark(session: Session, isBookmarked: Boolean) {
        bookmark(id = "session:${session.id}", isBookmarked)
    }

    override suspend fun bookmark(tag: Tag, isBookmarked: Boolean) {
        bookmark(id = "tag:${tag.id}", isBookmarked)
    }

    private suspend fun bookmark(id: String, isBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            prefs.edit {
                if (isBookmarked) {
                    Timber.i("Bookmarking $id")
                    putBoolean(id, true)
                } else {
                    remove(id)
                }
            }
        }
    }

    override suspend fun isBookmarked(content: Content): Boolean {
        return isBookmarked("content:${content.id}")
    }

    override suspend fun isBookmarked(session: Session): Boolean {
        return isBookmarked("session:${session.id}")
    }

    override suspend fun isBookmarked(tag: Tag): Boolean {
        return isBookmarked("tag:${tag.id}")
    }

    private suspend fun isBookmarked(id: String): Boolean {
        return withContext(Dispatchers.IO) {
            prefs.contains(id)
        }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            prefs.edit {
                clear()
            }
        }
    }

    private fun SharedPreferences.edit(block: SharedPreferences.Editor.() -> Unit) {
        edit().apply {
            block()
            apply()
        }
    }

    companion object {
        const val PREF_NAME = "bookmarks_pref"
        private const val HAS_MIGRATED_KEY = "has_migrated"
    }
}
