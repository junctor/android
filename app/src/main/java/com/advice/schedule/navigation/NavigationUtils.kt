package com.advice.schedule.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.advice.core.local.MenuItem
import timber.log.Timber

/** String-route helper; named to avoid clashing with Navigation 2.8+ type-safe [NavController.navigate]. */
fun NavController.navigateTo(navigation: Navigation?) {
    if (navigation == null) {
        Timber.e("Navigation is null")
        return
    }
    try {
        navigate(navigation.destination())
    } catch (ex: Exception) {
        Timber.e(ex)
        try {
            android.widget.Toast
                .makeText(context, "Could not open screen", android.widget.Toast.LENGTH_SHORT)
                .show()
        } catch (_: Exception) {
            // Context may be unavailable during teardown.
        }
    }
}

fun NavController.onBackPressed() {
    if (currentDestination?.route != Navigation.Home.route()) {
        popBackStack()
    }
}

internal fun Navigation.withArguments(backStackEntry: NavBackStackEntry): Navigation =
    when (this) {
        is Navigation.Content -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.Document -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            copy(id = id)
        }

        is Navigation.Event -> {
            val conference =
                backStackEntry.arguments?.getString("conference") ?: error("conference is required")
            val contentId =
                backStackEntry.arguments?.getString("contentId") ?: error("contentId is required")
            val sessionId = backStackEntry.arguments?.getString("sessionId").orEmpty()
            copy(conference = conference, id = contentId, session = sessionId)
        }

        is Navigation.FAQ -> {
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(label = label)
        }

        is Navigation.Feedback -> {
            val id = backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val contentArg =
                backStackEntry.arguments?.getString("content")?.toLongOrNull()
                    ?: error("content is required")
            val content = contentArg.takeUnless { it == Navigation.Feedback.NO_CONTENT_ID }
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
            val ids =
                backStackEntry.arguments
                    ?.getString("ids")
                    ?.split(",")
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

        Navigation.PrivacyPolicy -> {
            Navigation.PrivacyPolicy
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

        is Navigation.Wifi -> {
            val id =
                backStackEntry.arguments?.getString("id")?.toLongOrNull() ?: error("id is required")
            val label = backStackEntry.arguments?.getString("label") ?: error("label is required")
            copy(id = id, label = label)
        }
    }

internal fun MenuItem.toNavigation(): Navigation? =
    when (this) {
        is MenuItem.Content -> Navigation.Content(label)
        is MenuItem.Divider -> null
        is MenuItem.Document -> Navigation.Document(documentId)
        is MenuItem.Feedback -> Navigation.Feedback(formId, content = null)
        is MenuItem.Menu -> Navigation.Menu(label, menuId)
        is MenuItem.Navigation -> function.toTypedNavigation(label)
        is MenuItem.Organization -> Navigation.Organizations(label, organizationId)
        is MenuItem.Schedule -> Navigation.Schedule(label, tags)
        is MenuItem.SectionHeading -> null
        is MenuItem.Wifi -> Navigation.Wifi(id, label)
        is MenuItem.Maps -> Navigation.Maps
        is MenuItem.Search -> Navigation.Search
    }

private fun String.toTypedNavigation(label: String): Navigation? =
    when (this) {
        "news" -> Navigation.News(label)
        "locations" -> Navigation.Locations(label)
        "people" -> Navigation.People(label)
        "products" -> Navigation.Products(label)
        "faq" -> Navigation.FAQ(label)
        else -> {
            Timber.e("Unknown menu navigation function: $this")
            null
        }
    }
