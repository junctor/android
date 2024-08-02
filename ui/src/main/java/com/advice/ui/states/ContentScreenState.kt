package com.advice.ui.states

import com.advice.core.local.Content

sealed class ContentScreenState {
    data object Loading: ContentScreenState()
    data class Success(val content: List<Content>): ContentScreenState()
}
