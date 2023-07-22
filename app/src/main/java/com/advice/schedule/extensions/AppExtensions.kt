package com.advice.schedule.extensions

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import timber.log.Timber


@Composable
inline fun <reified VM : ViewModel> NavHostController.navGraphViewModel(): VM {
    val navBackStackEntry by currentBackStackEntryAsState()
    return navBackStackEntry?.let {
        viewModel(viewModelStoreOwner = it)
    } ?: viewModel<VM>().also {
        Timber.e("Creating new ViewModel: ${VM::class.java.simpleName}")
    }
}