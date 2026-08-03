package com.advice.schedule.navigation

/**
 * Parses notification / external deep-link query params into in-app [Navigation] destinations.
 */
object DeepLinkParser {
    fun parse(
        pathSegment: String?,
        conference: String?,
        event: String?,
        documentId: String?,
    ): Navigation? {
        return when (pathSegment) {
            "document" -> {
                if (conference.isNullOrBlank()) return null
                val id = documentId?.toLongOrNull() ?: return null
                Navigation.Document(id)
            }

            else -> {
                val code = conference ?: return null
                val eventParam = event ?: return null
                val parts = eventParam.split(":", limit = 2)
                val content = parts[0]
                val session = parts.getOrElse(1) { "" }
                Navigation.Event(code, content, session)
            }
        }
    }
}
