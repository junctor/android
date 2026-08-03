package com.advice.data.sources

/**
 * Koin `named(...)` qualifiers for the two [BookmarkedElementDataSource] stores.
 *
 * Filter tag selections and schedule favorites share the same interface but must
 * remain separate: "Clear filters" may wipe [FILTER_SELECTIONS] only and must not
 * touch persisted [EVENT_BOOKMARKS].
 */
object BookmarkDataSourceQualifiers {
    /** In-memory store for schedule filter / tag selections. */
    const val FILTER_SELECTIONS = "filter_selections"

    /** Persisted store for session and content bookmarks. */
    const val EVENT_BOOKMARKS = "event_bookmarks"
}
