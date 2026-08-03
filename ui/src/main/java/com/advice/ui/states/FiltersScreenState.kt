package com.advice.ui.states

import com.advice.core.local.TagType

sealed class FiltersScreenState {
    data object Loading : FiltersScreenState()

    data class Success(
        val filters: List<TagType>,
        val isBookmarkSelected: Boolean = false,
    ) : FiltersScreenState()
}
