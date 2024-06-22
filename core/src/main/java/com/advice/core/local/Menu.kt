package com.advice.core.local

data class Menu(
    val id: Long,
    val label: String,
    val items: List<MenuItem>,
)

sealed class MenuItem(
    val icon: String?,
    val label: String,
    val description: String? = null,
) {
    class SectionHeading(
        label: String,
    ) : MenuItem(null, label)

    object Divider : MenuItem(null, "")

    class Document(
        icon: String,
        label: String,
        description: String?,
        val documentId: Int,
    ) : MenuItem(icon, label, description)

    class Menu(
        icon: String,
        label: String,
        description: String?,
        val menuId: Int,
    ) : MenuItem(icon, label, description)

    class Navigation(
        icon: String,
        label: String,
        description: String?,
        val function: String,
    ) : MenuItem(icon, label, description)

    class Organization(
        icon: String,
        label: String,
        description: String?,
        val organizationId: Int,
    ) : MenuItem(icon, label, description)

    class Schedule(
        icon: String,
        label: String,
        description: String?,
        val tags: List<Int>,
    ) : MenuItem(icon, label, description)

    class Content(
        icon: String,
        label: String,
        description: String?,
    ) : MenuItem(icon, label, description)

    // todo: this should have a sealed class to map with NavHost
    val url: String?
        get() = when (this) {
            Divider -> null
            is Document -> "document/$documentId"
            is Menu -> "menu/$label/$menuId"
            is Navigation -> "$function/$label"
            is Organization -> "organizations/$label/$organizationId"
            is Schedule -> "schedule/$label/${tags.joinToString(",")}"
            is Content -> "content/$label"
            is SectionHeading -> null
        }
}
