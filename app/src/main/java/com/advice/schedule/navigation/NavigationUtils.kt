package com.advice.schedule.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.advice.core.local.MenuItem
import timber.log.Timber

fun NavController.navigate(navigation: Navigation?) {
    if (navigation == null) {
        Timber.e("Navigation is null")
        return
    }
    try {
        navigate(navigation.destination())
    } catch (ex: Exception) {
        Timber.e(ex)
    }
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
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.Event -> {
            val conference =
                backStackEntry.arguments?.getString("conference") ?: error("conference is required")
            val id = backStackEntry.arguments?.getString("id") ?: error("id is required")
            val (content, session) = id.split("-")
            copy(conference = conference, id = content, session = session)
        }

        is Navigation.FAQ -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.Feedback -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val content = backStackEntry.arguments?.getString("content")?.toLongOrNull() ?: error("content is required")
            copy(id = id, content = content)
        }

        Navigation.Home -> {
            Navigation.Home
        }

        is Navigation.Location -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
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
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(id = id, label = label)
        }

        is Navigation.Function -> {
            val function =
                backStackEntry.arguments?.getString("function") ?: error("function is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(function = function, label = label)
        }

        is Navigation.Product -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.News -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.Organization -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.Organizations -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
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
            val ids = backStackEntry.arguments?.getString("ids")?.split(",")
                ?.mapNotNull { it.toLongOrNull() } ?: error("ids is required")
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
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val name = backStackEntry.arguments?.getString("name") ?: error("name is required")
            copy(id = id, name = name)
        }

        is Navigation.Tag -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(id = id, label = label)
        }

        Navigation.Wifi -> {
            Navigation.Wifi
        }
    }
}

internal fun MenuItem.toNavigation(): Navigation? {
    return when (this) {
        is MenuItem.Content -> Navigation.Content(label)
        is MenuItem.Divider -> null
        is MenuItem.Document -> Navigation.Document(documentId)
        is MenuItem.Menu -> Navigation.Menu(label, menuId)
        is MenuItem.Navigation -> Navigation.Function(function, label)
        is MenuItem.Organization -> Navigation.Organizations(label, organizationId)
        is MenuItem.Schedule -> Navigation.Schedule(label, tags)
        is MenuItem.SectionHeading -> null
    }
}
