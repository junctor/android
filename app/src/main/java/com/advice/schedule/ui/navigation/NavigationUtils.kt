package com.advice.schedule.ui.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import timber.log.Timber

fun NavController.navigate(navigation: Navigation) {
    navigate(navigation.destination())
}

internal fun Navigation.withArguments(backStackEntry: NavBackStackEntry): Navigation {
    return when (this) {
        is Navigation.Content -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.ContentDetails -> {
            val conference =
                backStackEntry.arguments?.getString("conference") ?: error("conference is required")
            val id = backStackEntry.arguments?.getString("id") ?: error("id is required")
            copy(conference = conference, id = id)
        }

        is Navigation.Document -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.Event -> {
            val conference =
                backStackEntry.arguments?.getString("conference") ?: error("conference is required")
            val id = backStackEntry.arguments?.getString("id") ?: error("id is required")

            Timber.e("Navigating to event $conference $id")

            val (content, session) = id.split("-")
            copy(conference = conference, id = content, session = session)
        }

        is Navigation.FAQ -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        Navigation.Feedback -> {
            Navigation.Feedback
        }

        Navigation.Home -> {
            Navigation.Home
        }

        is Navigation.Location -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(id = id, label = label)
        }

        is Navigation.Locations -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        Navigation.Maps -> {
            Navigation.Maps
        }

        is Navigation.Menu -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(id = id, label = label)
        }

        is Navigation.Product -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.News -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.Organization -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.Organizations -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label, id = id)
        }

        is Navigation.People -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.Products -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.ProductsSummary -> {
            Navigation.ProductsSummary
        }

        is Navigation.Schedule -> {
            val ids = backStackEntry.arguments?.getString("ids")?.split(",")?.mapNotNull { it.toLongOrNull() } ?: error("ids is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(ids = ids, label = label)
        }

        Navigation.Search -> {
            Navigation.Search
        }

        Navigation.Settings -> {
            Navigation.Settings
        }

        is Navigation.Speaker -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val name = backStackEntry.arguments?.getString("name") ?: error("name is required")
            copy(id = id, name = name)
        }

        is Navigation.Tag -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(id = id, label = label)
        }

        Navigation.Wifi -> {
            Navigation.Wifi
        }
    }
}
