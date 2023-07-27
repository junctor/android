package com.advice.core.ui

import com.advice.core.local.TagType

sealed class FiltersScreenState {

    object Init : FiltersScreenState()

    class Success(val filters: List<TagType>) : FiltersScreenState()
}
