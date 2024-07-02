package com.advice.schedule.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.advice.feedback.ui.screens.Feedback
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.screens.Contents
import com.advice.schedule.ui.screens.Document
import com.advice.schedule.ui.screens.Event
import com.advice.schedule.ui.screens.FAQ
import com.advice.schedule.ui.screens.Home
import com.advice.schedule.ui.screens.Location
import com.advice.schedule.ui.screens.Locations
import com.advice.schedule.ui.screens.Maps
import com.advice.schedule.ui.screens.Menu
import com.advice.schedule.ui.screens.News
import com.advice.schedule.ui.screens.Organization
import com.advice.schedule.ui.screens.Organizations
import com.advice.schedule.ui.screens.Product
import com.advice.schedule.ui.screens.Products
import com.advice.schedule.ui.screens.Search
import com.advice.schedule.ui.screens.Settings
import com.advice.schedule.ui.screens.Speaker
import com.advice.schedule.ui.screens.Speakers
import com.advice.schedule.ui.screens.Tag
import com.advice.schedule.ui.screens.Tags
import com.advice.schedule.ui.screens.Wifi

@Composable
internal fun NavigationManager.setRoutes(navController: NavHostController) {
    set(navController, startDestination = Navigation.Home) {
        // Home
        register(Navigation.Home) {
            Home(navController)
        }
        // Maps
        register(Navigation.Maps) {
            Maps(navController)
        }
        // News
        register(Navigation.News()) {
            News(navController, it.label)
        }
        // Search
        register(Navigation.Search) {
            Search(navController)
        }
        // Locations
        register(Navigation.Locations()) {
            Locations(navController)
        }
        register(Navigation.Location()) {
            Location(navController, it.id, it.label)
        }

        // Schedule
        register(Navigation.Schedule()) {
            Tags(navController, it.ids, it.label)
        }
        // Events
        register(Navigation.Event()) {
            Event(navController, it.conference, it.id, it.session)
        }
        // Content
        register(Navigation.Content()) {
            Contents(navController, it.label)
        }
        // Tags
        register(Navigation.Tag()) {
            Tag(navController, it.id, it.label)
        }

        register(Navigation.Settings) {
            Settings(navController)
        }

        register(Navigation.Wifi) {
            Wifi(navController)
        }

        register(Navigation.Menu()) {
            Menu(navController, it.label, it.id)
        }

        register(Navigation.Document()) {
            Document(navController, it.id)
        }

        register(Navigation.FAQ()) {
            FAQ(navController)
        }

        // Organizations
        register(Navigation.Organizations()) {
            Organizations(navController, it.label, it.id)
        }

        register(Navigation.Organization()) {
            Organization(navController, it.id)
        }

        // People / Speakers
        register(Navigation.People()) {
            Speakers(navController, it.label)
        }

        register(Navigation.Speaker()) {
            Speaker(navController, it.id, it.name) { url ->
                (navController.context as MainActivity).openLink(url)
            }
        }

        // Merch
        register(Navigation.Products()) {
            Products(navController)
        }

        register(Navigation.Merch()) {
            Product(navController, it.id)
        }

        // Feedback
        register(Navigation.Feedback) {
            Feedback(navController)
        }
    }
}
