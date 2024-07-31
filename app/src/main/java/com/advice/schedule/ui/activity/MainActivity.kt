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
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.core.utils.Storage
import com.advice.firebase.extensions.document_cache_reads
import com.advice.firebase.extensions.document_reads
import com.advice.firebase.extensions.listeners_count
import com.advice.play.AppManager
import com.advice.reminder.ReminderManager
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.setRoutes
import com.advice.schedule.ui.components.NotificationsPopup
import com.advice.schedule.ui.viewmodels.MainViewModel
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.shortstack.hackertracker.BuildConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainActivity : AppCompatActivity(), KoinComponent {

    private val appManager by inject<AppManager>()
    private val storage by inject<Storage>()
    private val navigation by inject<NavigationManager>()
    private val analytics by inject<FirebaseAnalytics>()

    private val reminder by inject<ReminderManager>()

    // todo: fix this - this is a hack to get the navController to work
    private lateinit var navController: NavController

    private lateinit var mainViewModel: MainViewModel

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            analytics.logEvent(
                "permission_granted", bundleOf(
                    "permission" to "POST_NOTIFICATIONS"
                )
            )
            Timber.i("Permission granted")
        } else {
            analytics.logEvent(
                "permission_denied", bundleOf(
                    "permission" to "POST_NOTIFICATIONS"
                )
            )
            Timber.i("Permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Only showing the prompt once per version.
        if (storage.updateVersion != BuildConfig.VERSION_CODE) {
            appManager.checkForUpdate(this, REQUEST_CODE_UPDATE)
        }

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

            val state = mainViewModel.state.collectAsState().value

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
        var route = navDestination.route
            ?.replace("/{label}", "")
            ?.replace("//", "/") ?: return

        args?.keySet()?.forEach {
            val value = args.get(it)
            if (value != null && value is String) {
                route = route.replace("{${it}}", value)
            }
        }

        Timber.i("navigating to: $route")
        FirebaseAnalytics.getInstance(this).logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, route)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, MainActivity::class.java.name)
        }
    }

    @Deprecated("This is deprecated in favor of registerForActivityResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_UPDATE && resultCode != RESULT_OK) {
            Timber.e("Update flow failed! Result code: $resultCode")
            // Storing the version code so we don't keep asking for updates.
            storage.updateVersion = BuildConfig.VERSION_CODE
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
            analytics.logEvent(
                "request_permission", bundleOf(
                    "permission" to "POST_NOTIFICATIONS"
                )
            )
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
        if (intent.data != null) {
            val uri: Uri? = intent.data
            Timber.i("onNewIntent: $uri")
            analytics.logEvent(
                "open_deep_link",
                bundleOf(
                    "uri" to uri.toString()
                )
            )
            val conference = uri?.getQueryParameter("c") ?: return
            val event = uri.getQueryParameter("e") ?: return
            val (content, session) = if (event.contains(":")) {
                event.split(":")
            } else {
                listOf(event, "")
            }
            navController.navigate(Navigation.Event(conference, content, session))
        }
    }

    fun openLink(url: String) {
        Timber.i("Opening link: $url")
        analytics.logEvent("open_link", bundleOf("url" to url))
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }

    override fun onPause() {
        with(analytics) {
            logEvent(
                "session_document_read", bundleOf(
                    "total_document_reads" to document_reads,
                    "total_document_cache_reads" to document_cache_reads,
                    "total_listeners_count" to listeners_count
                )
            )
            document_reads = 0
            document_cache_reads = 0
            listeners_count = 0
        }
        super.onPause()
    }

    companion object {
        private const val REQUEST_CODE_UPDATE = 9003
    }
}
