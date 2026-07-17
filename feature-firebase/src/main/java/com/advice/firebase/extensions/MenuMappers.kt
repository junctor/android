package com.advice.firebase.extensions

import com.advice.core.local.Menu
import com.advice.core.local.MenuItem
import com.advice.core.local.Tag
import com.advice.firebase.models.menu.FirebaseMenu
import com.advice.firebase.models.menu.FirebaseMenuItem
import timber.log.Timber

fun FirebaseMenu.toMenu(): Menu? =
    try {
        Menu(
            id,
            titleText,
            items
                .sortedBy { it.sortOrder }
                .mapNotNull { it.toMenuItem() },
        )
    } catch (ex: Exception) {
        Timber.e("Could not map data to Menu: ${ex.message}")
        null
    }

fun FirebaseMenuItem.toMenuItem(): MenuItem? =
    try {
        when (function) {
            "section_heading" ->
                MenuItem.SectionHeading(
                    titleText,
                )

            "divider" -> MenuItem.Divider

            "document" ->
                MenuItem.Document(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    documentId ?: error("null document id: $titleText"),
                )

            "schedule" -> {
                MenuItem.Schedule(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    appliedTagIds,
                )
            }

            "schedule_bookmark" -> {
                MenuItem.Schedule(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    listOf(Tag.bookmark.id),
                )
            }

            "menu" ->
                MenuItem.Menu(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    menuId ?: error("null menu id: $titleText"),
                )

            "people", "locations", "products", "news", "faq" ->
                MenuItem.Navigation(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    function,
                )

            "organizations" ->
                MenuItem.Organization(
                    googleMaterialsymbol,
                    titleText,
                    description,
                    appliedTagIds.first(),
                )

            "content" ->
                MenuItem.Content(
                    googleMaterialsymbol,
                    titleText,
                    description,
                )

            "maps" ->
                MenuItem.Maps(
                    googleMaterialsymbol,
                    titleText,
                    description,
                )

            "search" -> MenuItem.Search(
                googleMaterialsymbol,
                titleText,
                description,
            )

            else -> error("Unknown menu item function: $titleText, $function")
        }
    } catch (ex: Exception) {
        Timber.e("Could not map data to MenuItem: ${ex.message}")
        null
    }
