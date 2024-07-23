package com.advice.schedule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import timber.log.Timber

class NavigationManager(
    private val analytics: FirebaseAnalytics,
) {
    @Composable
    fun set(
        navController: NavHostController,
        startDestination: Navigation.Home,
        block: NavGraphBuilder.() -> Unit
    ) {
        androidx.navigation.compose.NavHost(
            navController = navController,
            startDestination = startDestination.destination(),
        ) {
            block()
        }
    }

    fun <T : Navigation> NavGraphBuilder.register(
        navigation: T,
        content: @Composable (T) -> Unit
    ) {
        Timber.i("Registering: ${navigation.route()}")
        composable(navigation.route()) {
            val arguments = navigation.withArguments(it) as T
            Timber.i("Rendering: ${arguments.destination()}")
            analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
                param(FirebaseAnalytics.Param.SCREEN_NAME, arguments.destination())
                param(FirebaseAnalytics.Param.SCREEN_CLASS, navigation::class.java.name)
            }
            content(arguments)
        }
    }
}
