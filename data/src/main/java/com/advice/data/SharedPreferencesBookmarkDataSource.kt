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

class SharedPreferencesBookmarkDataSource(
    private val context: Context,
) : BookmarkedElementDataSource {
    companion object {
        const val PREF_NAME = "bookmarks_pref"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    init {
        // Clearing out any unneeded bookmarks
        getBookmarks()
            .filter {
                !it.value
            }.forEach {
                prefs.edit().remove(it.id).apply()
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

    private fun getBookmarks(): List<Bookmark> = prefs.all.map { Bookmark(it.key, it.value as Boolean) }

    override suspend fun clear() {
        withContext(Dispatchers.IO) {
            prefs.edit().clear().apply()
        }
    }

    override suspend fun bookmark(
        id: Long,
        isBookmarked: Boolean,
    ) {
        withContext(Dispatchers.IO) {
            prefs.edit().apply {
                if (isBookmarked) {
                    putBoolean(id.toString(), true)
                } else {
                    remove(id.toString())
                }
                apply()
            }
        }
    }
}
