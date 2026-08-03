package com.advice.schedule.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.koin.androidx.compose.koinViewModel

@Composable
inline fun <reified VM : ViewModel> NavHostController.navGraphViewModel(): VM {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.let {
        koinViewModel(viewModelStoreOwner = it)
    } ?: koinViewModel()
}
