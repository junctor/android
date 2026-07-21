package com.advice.schedule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.advice.schedule.ui.activity.MainActivity
import com.advice.schedule.ui.screens.Contents
import com.advice.schedule.ui.screens.Document
import com.advice.schedule.ui.screens.Event
import com.advice.schedule.ui.screens.FAQ
import com.advice.schedule.ui.screens.Feedback
import com.advice.schedule.ui.screens.Home
import com.advice.schedule.ui.screens.Location
import com.advice.schedule.ui.screens.Locations
import com.advice.schedule.ui.screens.Maps
import com.advice.schedule.ui.screens.Menu
import com.advice.schedule.ui.screens.News
import com.advice.schedule.ui.screens.Organization
import com.advice.schedule.ui.screens.Organizations
import com.advice.schedule.ui.screens.PrivacyPolicy
import com.advice.schedule.ui.screens.Product
import com.advice.schedule.ui.screens.Products
import com.advice.schedule.ui.screens.ProductsSummary
import com.advice.schedule.ui.screens.Search
import com.advice.schedule.ui.screens.Settings
import com.advice.schedule.ui.screens.Speaker
import com.advice.schedule.ui.screens.Speakers
import com.advice.schedule.ui.screens.Tag
import com.advice.schedule.ui.screens.Tags
import com.advice.schedule.ui.screens.Wifi

@Composable
fun NavigationManager.SetRoutes(
    activity: MainActivity,
    navController: NavHostController,
) {
    NavHost(navController = navController, startDestination = Navigation.Home.route()) {
        register(Navigation.Home) {
            Home(activity, navController)
        }
        register(Navigation.Maps) {
            Maps(navController)
        }
        register(Navigation.News()) {
            News(navController, it.label)
        }
        register(Navigation.Search) {
            Search(navController)
        }
        register(Navigation.Locations()) {
            Locations(navController)
        }
        register(Navigation.Event()) {
            Event(activity, navController, it.conference, it.id, it.session)
        }
        register(Navigation.Content()) {
            Contents(activity, navController, it.label)
        }
        register(Navigation.Location()) {
            Location(activity, navController, it.id, it.label)
        }
        register(Navigation.Tag()) {
            Tag(activity, navController, it.id, it.label)
        }
        register(Navigation.Schedule()) {
            Tags(activity, navController, it.ids, it.label)
        }
        register(Navigation.Speaker()) {
            Speaker(navController, it.id, it.name, activity::openLink)
        }
        register(Navigation.Settings) {
            Settings(navController)
        }
        register(Navigation.PrivacyPolicy) {
            PrivacyPolicy(navController)
        }
        register(Navigation.Wifi()) {
            Wifi(navController, it.id)
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
        register(Navigation.Organizations()) {
            Organizations(navController, it.label, it.id)
        }
        register(Navigation.Organization()) {
            Organization(navController, it.id)
        }
        register(Navigation.People()) {
            Speakers(navController, it.label)
        }
        register(Navigation.Products()) {
            Products(activity, navController, it.label)
        }
        register(Navigation.Product()) {
            Product(activity, navController, it.id)
        }
        register(Navigation.ProductsSummary) {
            ProductsSummary(activity, navController)
        }
        register(Navigation.Feedback()) {
            Feedback(navController, it.id, it.content)
        }
    }
}
