package com.advice.schedule.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.setRoutes
import com.advice.schedule.ui.components.NotificationsPopup
import com.advice.schedule.ui.viewmodels.MainViewModel
import com.advice.schedule.ui.viewmodels.MainViewState
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MainActivity : AppCompatActivity(), KoinComponent {

    private val navigation by inject<NavigationManager>()

    // todo: fix this - this is a hack to get the navController to work
    private lateinit var navController: NavController
    private lateinit var mainViewModel: MainViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        mainViewModel.onPermissionResult(isGranted)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = false
                )
            }

            navController = rememberNavController()
            navController.addOnDestinationChangedListener { _, navDestination, args ->
                onDestinationChanged(navDestination, args)
            }

            mainViewModel = viewModel<MainViewModel>()
            mainViewModel.onAppStart(this)

            val state = mainViewModel.state.collectAsState(MainViewState()).value

            ScheduleTheme {
                navigation.setRoutes(this, navController = navController as NavHostController)

                if (state.permissionDialog) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f))
                            .zIndex(10F),
                        contentAlignment = Alignment.Center,
                    ) {
                        Popup(
                            alignment = Alignment.Center,
                            properties = PopupProperties(
                                excludeFromSystemGesture = true,
                            ),
                            onDismissRequest = {
                                mainViewModel.dismissPermissionDialog()
                            },
                        ) {
                            NotificationsPopup(
                                hasPermission = hasNotificationPermission(),
                                onRequestPermission = {
                                    requestNotificationPermissionLegacy()
                                    mainViewModel.dismissPermissionDialog()
                                },
                                onDismiss = {
                                    mainViewModel.dismissPermissionDialog()
                                },
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Mapping the navDestination to a label for analytics. Will replace the arguments in the route with the navArgs
     * and also remove any labels to clean up the route.
     */
    private fun onDestinationChanged(
        navDestination: NavDestination,
        args: Bundle?
    ) {
        mainViewModel.onDestinationChanged(navDestination, args)
    }

    @Deprecated("This is deprecated in favor of registerForActivityResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UPDATE && resultCode != RESULT_OK) {
            mainViewModel.onAppUpdateRequest(resultCode)
        }
    }

    /**
     * When the user bookmarks any event we want to display the notification permission dialog.
     * If the user already has granted us the permission, or doesn't require it, we still show
     * the dialog to inform them about the reminder feature.
     */
    fun onBookmarkEvent() {
        if (!mainViewModel.hasSeenNotificationPopup()) {
            mainViewModel.showPermissionDialog()
        }
    }

    private fun requestNotificationPermissionLegacy() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            android.Manifest.permission.POST_NOTIFICATIONS
        } else {
            return
        }
        if (!hasPermission(permission)) {
            mainViewModel.onPermissionRequest()
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }
    }

    /**
     * Returns true if the permission is granted.
     */
    private fun hasPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val uri = intent.data
        if (uri != null) {
            val destination = mainViewModel.onNewIntent(uri)
            navController.navigate(destination)
        }
    }

    fun openLink(url: String) {
        mainViewModel.onLinkOpen(url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onPause() {
        mainViewModel.onPause()
        super.onPause()
    }

    companion object {
        internal const val REQUEST_CODE_UPDATE = 9003
    }
}
