package com.advice.core.ui

import com.advice.core.local.TagType

sealed class FiltersScreenState {
    data object Loading : FiltersScreenState()

    data class Success(
        val filters: List<TagType>,
        val isBookmarkSelected: Boolean = false,
    ) : FiltersScreenState()
}
