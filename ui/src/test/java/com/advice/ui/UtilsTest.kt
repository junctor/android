package com.advice.ui

import androidx.compose.ui.graphics.Color
import com.advice.ui.utils.parseColor
import com.advice.ui.utils.toResource
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class UtilsTest {

    @Test
    fun `verify parseColor returns default color`() = runBlocking {
        val result = parseColor(null)
        assertEquals(Color.Blue, result)
    }

    @Test
    fun `verify parseColor returns default color on exception`() = runBlocking {
        val result = parseColor("invalid")
        assertEquals(Color.Green, result)
    }

    @Test
    fun `verify balance returns drawable`() = runBlocking {
        val result = "balance".toResource()
        assertEquals(R.drawable.baseline_balance_24, result)
    }

    @Test
    fun `verify calendar_month returns drawable`() = runBlocking {
        val result = "calendar_month".toResource()
        assertEquals(R.drawable.baseline_calendar_month_24, result)
    }

    @Test
    fun `verify description returns drawable`() = runBlocking {
        val result = "description".toResource()
        assertEquals(R.drawable.outline_description_24, result)
    }

    @Test
    fun `verify directions_bus returns drawable`() = runBlocking {
        val result = "directions_bus".toResource()
        assertEquals(R.drawable.baseline_directions_bus_24, result)
    }

    @Test
    fun `verify directions_walk returns drawable`() = runBlocking {
        val result = "directions_walk".toResource()
        assertEquals(R.drawable.baseline_directions_walk_24, result)
    }

    @Test
    fun `verify door_open returns drawable`() = runBlocking {
        val result = "door_open".toResource()
        assertEquals(R.drawable.baseline_door_open_24, result)
    }

    @Test
    fun `verify escalator_warning returns drawable`() = runBlocking {
        val result = "escalator_warning".toResource()
        assertEquals(R.drawable.baseline_escalator_warning_24, result)
    }

    @Test
    fun `verify flag returns drawable`() = runBlocking {
        val result = "flag".toResource()
        assertEquals(R.drawable.baseline_flag_24, result)
    }

    @Test
    fun `verify groups returns drawable`() = runBlocking {
        val result = "groups".toResource()
        assertEquals(R.drawable.baseline_groups_24, result)
    }

    @Test
    fun `verify help returns drawable`() = runBlocking {
        val result = "help".toResource()
        assertEquals(R.drawable.baseline_help_24, result)
    }

    @Test
    fun `verify location_city returns drawable`() = runBlocking {
        val result = "location_city".toResource()
        assertEquals(R.drawable.baseline_location_city_24, result)
    }

    @Test
    fun `verify map returns drawable`() = runBlocking {
        val result = "map".toResource()
        assertEquals(R.drawable.baseline_map_24, result)
    }

    @Test
    fun `verify menu returns drawable`() = runBlocking {
        val result = "menu".toResource()
        assertEquals(R.drawable.baseline_menu_24, result)
    }

    @Test
    fun `verify news returns drawable`() = runBlocking {
        val result = "news".toResource()
        assertEquals(R.drawable.baseline_news_24, result)
    }

    @Test
    fun `verify point_of_sale returns drawable`() = runBlocking {
        val result = "point_of_sale".toResource()
        assertEquals(R.drawable.baseline_point_of_sale_24, result)
    }

    @Test
    fun `verify question_mark returns drawable`() = runBlocking {
        val result = "question_mark".toResource()
        assertEquals(R.drawable.baseline_question_mark_24, result)
    }

    @Test
    fun `verify restaurant_menu returns drawable`() = runBlocking {
        val result = "restaurant_menu".toResource()
        assertEquals(R.drawable.baseline_restaurant_menu_24, result)
    }

    @Test
    fun `verify search returns drawable`() = runBlocking {
        val result = "search".toResource()
        assertEquals(R.drawable.baseline_search_24, result)
    }

    @Test
    fun `verify shopping_bag returns drawable`() = runBlocking {
        val result = "shopping_bag".toResource()
        assertEquals(R.drawable.baseline_shopping_bag_24, result)
    }

    @Test
    fun `verify sprint returns drawable`() = runBlocking {
        val result = "sprint".toResource()
        assertEquals(R.drawable.baseline_sprint_24, result)
    }

    @Test
    fun `verify tv returns drawable`() = runBlocking {
        val result = "tv".toResource()
        assertEquals(R.drawable.baseline_tv_24, result)
    }

    @Test
    fun `verify wallpaper_slideshow returns drawable`() = runBlocking {
        val result = "wallpaper_slideshow".toResource()
        assertEquals(R.drawable.baseline_wallpaper_slideshow_24, result)
    }

    @Test
    fun `verify wifi returns drawable`() = runBlocking {
        val result = "wifi".toResource()
        assertEquals(R.drawable.baseline_wifi_24, result)
    }

    @Test
    fun `verify unknown icon returns null`() = runBlocking {
        val result = "unknown".toResource()
        assertEquals(null, result)
    }
}
