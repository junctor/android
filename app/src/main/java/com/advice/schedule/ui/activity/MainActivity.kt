package com.advice.schedule.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.analytics.core.AnalyticsProvider
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.navigation.SetRoutes
import com.advice.schedule.navigation.navigateTo
import com.advice.schedule.ui.components.EmergencyBanner
import com.advice.schedule.ui.viewmodels.MainViewModel
import com.advice.schedule.ui.viewmodels.MainViewState
import com.advice.ui.components.notifications.NotificationsPopup
import com.advice.ui.components.notifications.PopupContainer
import com.advice.ui.theme.ScheduleTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainActivity : AppCompatActivity(), KoinComponent {

    private val navigation by inject<NavigationManager>()
    private val analytics by inject<AnalyticsProvider>()
    private val mainViewModel: MainViewModel by viewModels()

    /**
     * Bridge for deep links handled outside composition ([onNewIntent]).
     * Set/cleared from [DisposableEffect] so it is never held across teardown.
     */
    private var navController: NavHostController? = null
    private var pendingDeepLink: Uri? = null

    /**
     * Grant/deny is intentionally unused: the notification popup is dismissed and marked seen
     * before the system dialog returns, and no further product behavior depends on the result.
     */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op: popup already dismissed/marked seen */ }

    private val requestWirelessPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* join APIs do not require follow-up beyond grant state */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        pendingDeepLink = intent.data
        mainViewModel.onAppStart(this)

        setContent {
            val navController = rememberNavController()

            DisposableEffect(navController) {
                this@MainActivity.navController = navController
                val listener =
                    NavController.OnDestinationChangedListener { _, navDestination, args ->
                        onDestinationChanged(navDestination, args)
                    }
                navController.addOnDestinationChangedListener(listener)
                consumePendingDeepLink(navController)

                onDispose {
                    navController.removeOnDestinationChangedListener(listener)
                    if (this@MainActivity.navController === navController) {
                        this@MainActivity.navController = null
                    }
                }
            }

            ScheduleTheme {
                val state = mainViewModel.state.collectAsState(MainViewState()).value

                Column(
                    Modifier
                        .fillMaxSize()
                ) {
                    // Emergency banner that pushes content down
                    if (state.emergencyDocument != null) {
                        EmergencyBanner(state.emergencyDocument) {
                            navController.navigateTo(Navigation.Document(state.emergencyDocument.id))
                        }
                    }

                    // Main screen content
                    navigation.SetRoutes(
                        this@MainActivity,
                        navController = navController
                    )
                }

                if (state.permissionDialog) {
                    PopupContainer(
                        onDismiss = { mainViewModel.dismissPermissionDialog() },
                    ) {
                        NotificationsPopup(
                            hasPermission = hasNotificationPermission(),
                            onRequestPermission = {
                                requestNotificationPermission()
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

    private fun requestNotificationPermission() {
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
     * Runtime grants needed for the Wi-Fi join path on this API level.
     * On API 29+, [android.provider.Settings.ACTION_WIFI_ADD_NETWORKS] and
     * [android.net.wifi.WifiManager.addNetworkSuggestions] do not require location.
     * On API 28 and below, fine location is required to read configured networks.
     */
    fun hasWirelessPermissions(): Boolean {
        return requiredWirelessPermissions().all { hasPermission(it) }
    }

    fun requestWirelessPermissions() {
        val missing = requiredWirelessPermissions().filterNot { hasPermission(it) }
        if (missing.isNotEmpty()) {
            requestWirelessPermissionsLauncher.launch(missing.toTypedArray())
        }
    }

    private fun requiredWirelessPermissions(): Array<String> {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            emptyArray()
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
        setIntent(intent)
        val uri = intent.data
        Timber.i("onNewIntent: $uri")
        if (uri == null) return

        val controller = navController
        if (controller != null) {
            navigateDeepLink(controller, uri)
        } else {
            pendingDeepLink = uri
        }
    }

    private fun consumePendingDeepLink(controller: NavHostController) {
        val uri = pendingDeepLink ?: return
        pendingDeepLink = null
        navigateDeepLink(controller, uri)
    }

    private fun navigateDeepLink(controller: NavHostController, uri: Uri) {
        try {
            val destination = getDestination(uri) ?: return
            controller.navigateTo(destination)

            analytics.logEvent(
                "open_deep_link",
                Bundle().apply {
                    putString("uri", uri.toString())
                }
            )
        } catch (ex: Exception) {
            Timber.e(ex, "Could not navigate to deep link: $uri")
        }
    }

    private fun getDestination(uri: Uri): Navigation? {
        return when (uri.lastPathSegment) {
            "document" -> {
                val id = uri.getQueryParameter("id")?.toLongOrNull() ?: return null
                Navigation.Document(id)
            }

            else -> {
                val conference = uri.getQueryParameter("c") ?: return null
                val event = uri.getQueryParameter("e") ?: return null
                val (content, session) = if (event.contains(":")) {
                    event.split(":")
                } else {
                    listOf(event, "")
                }
                Navigation.Event(conference, content, session)
            }
        }
    }

    fun openLink(url: String) {
        try {
            mainViewModel.onLinkOpen(url)
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        } catch (ex: Exception) {
            Timber.e(ex)
            Toast.makeText(this, "Could not open link", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        try {
            mainViewModel.onPause()
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        super.onPause()
    }

    companion object {
        internal const val REQUEST_CODE_UPDATE = 9003
    }
}
