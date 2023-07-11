package com.advice.data

import android.content.Context
import android.content.SharedPreferences
import com.advice.core.local.Bookmark
import com.advice.data.sources.BookmarkedElementDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class SharedPreferencesBookmarkDataSource(private val context: Context) : BookmarkedElementDataSource {

    companion object {
        const val PREF_NAME = "bookmarks_pref"
    }

    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    override fun get(): Flow<List<Bookmark>> = callbackFlow {
        val allEntries = prefs.all
        val bookmarks = allEntries.map { Bookmark(it.key, it.value as Boolean) }
        trySend(bookmarks).isSuccess

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            val allUpdatedEntries = prefs.all
            val updatedBookmarks = allUpdatedEntries.map { Bookmark(it.key, it.value as Boolean) }
            trySend(updatedBookmarks).isSuccess
        }

        prefs.registerOnSharedPreferenceChangeListener(listener)

        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            prefs.edit().clear().apply()
        }
    }

    override suspend fun bookmark(id: Long, isBookmarked: Boolean) {
        withContext(Dispatchers.IO) {
            prefs.edit().putBoolean(id.toString(), isBookmarked).apply()
        }
    }
}
