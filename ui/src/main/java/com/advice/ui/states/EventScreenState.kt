package com.advice.ui.states

import com.advice.core.local.Content
import com.advice.core.local.Session

sealed class EventScreenState {
    object Loading : EventScreenState()

    data class Success(
        val content: Content,
        val session: Session?,
        val relatedContent: List<Content> = emptyList(),
    ) : EventScreenState()

    data class Error(val message: String) : EventScreenState()
}
