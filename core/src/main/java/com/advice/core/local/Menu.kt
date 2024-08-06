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

    data object Divider : MenuItem(null, "")

    class Document(
        icon: String,
        label: String,
        description: String?,
        val documentId: Long,
    ) : MenuItem(icon, label, description)

    class Menu(
        icon: String,
        label: String,
        description: String?,
        val menuId: Long,
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
        val organizationId: Long,
    ) : MenuItem(icon, label, description)

    class Schedule(
        icon: String,
        label: String,
        description: String?,
        val tags: List<Long>,
    ) : MenuItem(icon, label, description)

    class Content(
        icon: String,
        label: String,
        description: String?,
    ) : MenuItem(icon, label, description)

    class Wifi(
        label: String,
        description: String?,
        val id: Long,
    ) : MenuItem("wifi", label, description)

    class Maps(
        icon: String,
        label: String,
        description: String?,
    ) : MenuItem(icon, label, description)
}
