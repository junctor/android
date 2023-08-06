package com.advice.core.local

data class Menu(
    val id: Long,
    val label: String,
    val items: List<MenuItem>,
)

sealed class MenuItem(val icon: String?, val label: String) {
    class SectionHeading(
        label: String,
    ) : MenuItem(null, label)

    object Divider : MenuItem(null, "")

    class Document(
        icon: String,
        label: String,
        val documentId: Int,
    ) : MenuItem(icon, label)

    class Menu(
        icon: String,
        label: String,
        val menuId: Int,
    ) : MenuItem(icon, label)

    class Navigation(
        icon: String,
        label: String,
        val function: String,
    ) : MenuItem(icon, label)

    class Organization(
        icon: String,
        label: String,
        val organizationId: Int,
    ) : MenuItem(icon, label)

    class Schedule(
        icon: String,
        label: String,
        val tags: List<Int>,
    ) : MenuItem(icon, label)


    val url: String?
        get() = when (this) {
            Divider -> null
            is Document -> "document/$documentId"
            is Menu -> "menu/$label/$menuId"
            is Navigation -> "$function/$label"
            is Organization -> "organizations/$label/$organizationId"
            is Schedule -> "schedule/$label/${tags.joinToString(",")}"
            is SectionHeading -> null
        }
}
