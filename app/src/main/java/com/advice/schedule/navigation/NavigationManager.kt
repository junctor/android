package com.advice.schedule.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import timber.log.Timber

class NavigationManager {

    internal inline fun <reified T : Navigation> NavGraphBuilder.register(
        navigation: T,
        crossinline content: @Composable (T) -> Unit
    ) {
        Timber.i("Registering: ${navigation.route()}")
        composable(navigation.route()) {
            val arguments = navigation.withArguments(it)
            require(arguments is T) {
                "Expected ${T::class.simpleName}, got ${arguments::class.simpleName}"
            }
            content(arguments)
        }
    }
}
