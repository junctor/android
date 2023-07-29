package com.advice.core.local

data class Menu(
    val label: String,
    val items: List<MenuItem>,
)

sealed class MenuItem(val label: String) {
    class Document(
        label: String,
        val documentId: Int,
    ) : MenuItem(label)

    class Menu(
        label: String,
        val menuId: Int,
    ) : MenuItem(label)

    class Navigation(
        label: String,
        val function: String,
    ) : MenuItem(label)

    class Organization(
        label: String,
        val organizationId: Int,
    ) : MenuItem(label)

    class Schedule(
        label: String,
        val tags: List<Int>,
    ) : MenuItem(label)
}
