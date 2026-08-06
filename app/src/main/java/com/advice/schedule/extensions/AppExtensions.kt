package com.advice.schedule.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.androidx.compose.koinViewModel

/**
 * Scopes the ViewModel to the current nav back stack entry (cleared when the screen
 * leaves the back stack). Use this or plain [koinViewModel] for screen-local state;
 * pass `viewModelStoreOwner = activity` only when state must be shared across
 * screens or survive within the Activity (e.g. Home's shell ViewModels, merch cart).
 */
@Composable
inline fun <reified VM : ViewModel> NavHostController.navGraphViewModel(): VM {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.let {
        koinViewModel(viewModelStoreOwner = it)
    } ?: koinViewModel()
}
