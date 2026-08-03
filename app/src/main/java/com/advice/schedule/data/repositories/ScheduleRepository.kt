package com.advice.schedule.data.repositories

import com.advice.core.local.Bookmark
import com.advice.core.local.Content
import com.advice.core.local.Event
import com.advice.core.local.FlowResult
import com.advice.core.local.Session
import com.advice.core.local.Tag
import com.advice.core.local.TagType
import com.advice.core.ui.ScheduleFilter
import com.advice.data.sources.BookmarkedElementDataSource
import com.advice.schedule.domain.ContentBookmarkUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

sealed class ScheduleResult {
    data object Loading : ScheduleResult()

    data class Empty(
        val message: String,
    ) : ScheduleResult()

    data class Success(
        val events: List<Event>,
    ) : ScheduleResult()

    data class Error(
        val message: String,
    ) : ScheduleResult()
}

class ScheduleRepository(
    private val contentRepository: ContentRepository,
    private val tagsRepository: TagsRepository,
    private val contentBookmarkUseCase: ContentBookmarkUseCase,
    /** Filter selections (incl. bookmark chip); not persisted event favorites. */
    private val filterSelectionsDataSource: BookmarkedElementDataSource,
) {
    fun getSchedule(filter: ScheduleFilter): Flow<ScheduleResult> {
        return combine(
            contentRepository.content,
            tagsRepository.tags,
            filterSelectionsDataSource.get(),
        ) { contentResult, tagsResult, bookmarks ->
            when {
                contentResult is FlowResult.Failure ->
                    return@combine ScheduleResult.Error(
                        contentResult.error.message ?: "Could not load schedule",
                    )
                tagsResult is FlowResult.Failure ->
                    return@combine ScheduleResult.Error(
                        tagsResult.error.message ?: "Could not load schedule filters",
                    )
                contentResult is FlowResult.Loading || tagsResult is FlowResult.Loading ->
                    return@combine ScheduleResult.Loading
                contentResult !is FlowResult.Success || tagsResult !is FlowResult.Success ->
                    return@combine ScheduleResult.Loading
            }

            val content = contentResult.value
            val tags = tagsResult.value

            val events: List<Event> =
                content.content.flatMap { item ->
                    item.sessions.map { session ->
                        Event(item, session)
                    }
                }

            val sortedEvents = events.sortedBy { it.session.start }
            val selected = tags.filter { it.tags.any { tag -> tag.isSelected } }
            val isBookmarkFilterSelected =
                bookmarks
                    .filterIsInstance<Bookmark.TagBookmark>()
                    .any { it.id == Tag.bookmark.id.toString() && it.value }

            val filteredEvents =
                when (filter) {
                    ScheduleFilter.Default -> {
                        filter(sortedEvents, selected, isBookmarkFilterSelected)
                    }

                    is ScheduleFilter.Location -> {
                        sortedEvents.filter { it.session.location.id == filter.id }
                    }

                    is ScheduleFilter.Tag -> {
                        if (filter.id == Tag.bookmark.id) {
                            sortedEvents.filter { it.session.isBookmarked }
                        } else {
                            sortedEvents.filter { it.types.any { type -> type.id == filter.id } }
                        }
                    }

                    is ScheduleFilter.Tags -> {
                        val ids = filter.ids ?: emptyList()
                        if (ids == listOf(Tag.bookmark.id)) {
                            sortedEvents.filter { it.session.isBookmarked }
                        } else {
                            sortedEvents.filter { it.types.any { type -> type.id in ids } }
                        }
                    }
                }

            if (filteredEvents.isEmpty()) {
                val defaultFilter = filter is ScheduleFilter.Default && isBookmarkFilterSelected
                val onlyBookmarks = selected.size == 1 && selected.any { it.id == Tag.bookmark.id }
                val filterByBookmarks =
                    (filter as? ScheduleFilter.Tag)?.id == Tag.bookmark.id ||
                        (filter as? ScheduleFilter.Tags)?.ids == listOf(Tag.bookmark.id)
                val isDisplayingBookmarks = defaultFilter || onlyBookmarks || filterByBookmarks
                val message =
                    when {
                        isDisplayingBookmarks -> {
                            "Bookmark events to see them here"
                        }

                        filter is ScheduleFilter.Location -> {
                            "No events found in this location"
                        }

                        filter is ScheduleFilter.Tag -> {
                            "No events found for ${filter.label}"
                        }

                        content.content.isEmpty() -> {
                            "No events found"
                        }

                        else -> {
                            "No events found with selected tags"
                        }
                    }

                return@combine ScheduleResult.Empty(message)
            }

            return@combine ScheduleResult.Success(filteredEvents)
        }
    }

    private fun filter(
        events: List<Event>,
        filter: List<TagType>,
        isBookmarkFilterSelected: Boolean,
    ): List<Event> {
        if (filter.isEmpty()) {
            if (isBookmarkFilterSelected) {
                return events.filter { it.session.isBookmarked }
            }
            return events
        }

        val groups =
            filter.map {
                it.tags.filter { tag -> tag.isSelected }.map { tag -> tag.id }
            }

        return events
            .filter {
                groups.all { ids ->
                    it.types.any { type -> type.id in ids }
                }
            }
    }

    suspend fun bookmark(
        content: Content,
        session: Session?,
        isBookmarked: Boolean,
    ) {
        contentBookmarkUseCase.bookmark(content, session, isBookmarked)
    }
}
