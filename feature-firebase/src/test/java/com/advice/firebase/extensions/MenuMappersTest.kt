package com.advice.firebase.extensions

import com.advice.core.local.MenuItem
import com.advice.core.local.Tag
import com.advice.firebase.models.menu.FirebaseMenuItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuMappersTest {
    @Test
    fun mapsKnownFunctions() {
        assertTrue(item(function = "section_heading").toMenuItem() is MenuItem.SectionHeading)
        assertEquals(MenuItem.Divider, item(function = "divider").toMenuItem())
        assertTrue(item(function = "document", documentId = 5).toMenuItem() is MenuItem.Document)
        assertTrue(item(function = "schedule").toMenuItem() is MenuItem.Schedule)
        assertTrue(item(function = "menu", menuId = 3).toMenuItem() is MenuItem.Menu)
        assertTrue(item(function = "news").toMenuItem() is MenuItem.Navigation)
        assertTrue(item(function = "organizations", appliedTagIds = listOf(9)).toMenuItem() is MenuItem.Organization)
        assertTrue(item(function = "content").toMenuItem() is MenuItem.Content)
        assertTrue(item(function = "maps").toMenuItem() is MenuItem.Maps)
        assertTrue(item(function = "search").toMenuItem() is MenuItem.Search)
    }

    @Test
    fun scheduleBookmark_usesBookmarkTagId() {
        val mapped = item(function = "schedule_bookmark").toMenuItem() as MenuItem.Schedule

        assertEquals(listOf(Tag.bookmark.id), mapped.tags)
    }

    @Test
    fun unknownFunction_returnsNull() {
        assertNull(item(function = "unknown_thing").toMenuItem())
    }

    @Test
    fun documentWithoutId_returnsNull() {
        assertNull(item(function = "document", documentId = null).toMenuItem())
    }

    private fun item(
        function: String,
        documentId: Long? = null,
        menuId: Long? = null,
        appliedTagIds: List<Long> = emptyList(),
    ) = FirebaseMenuItem(
        function = function,
        titleText = "Title",
        googleMaterialsymbol = "icon",
        documentId = documentId,
        menuId = menuId,
        appliedTagIds = appliedTagIds,
    )
}
