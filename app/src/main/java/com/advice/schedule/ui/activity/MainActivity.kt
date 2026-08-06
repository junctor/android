package com.advice.schedule.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.analytics.core.AnalyticsProvider
import com.advice.core.utils.ToastManager
import com.advice.data.session.UserSession
import com.advice.schedule.navigation.DeepLinkParser
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.navigation.SetRoutes
import com.advice.schedule.navigation.navigateTo
import com.advice.schedule.presentation.viewmodel.MainViewModel
import com.advice.schedule.presentation.viewmodel.MainViewState
import com.advice.schedule.ui.components.EmergencyBanner
import com.advice.ui.components.notifications.NotificationsPopup
import com.advice.ui.components.notifications.PopupContainer
import com.advice.ui.theme.ScheduleTheme
import com.advice.ui.utils.ClearEdgeToEdgeProtectionsEffect
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import android.graphics.Color as AndroidColor

class MainActivity : AppCompatActivity() {
    private val navigation: NavigationManager by inject()
    private val analytics: AnalyticsProvider by inject()
    private val userSession: UserSession by inject()
    private val toastManager: ToastManager by inject()
    private val mainViewModel: MainViewModel by viewModel()

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
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { /* no-op: popup already dismissed/marked seen */ }

    private val requestWirelessPermissionsLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions(),
        ) { /* join APIs do not require follow-up beyond grant state */ }

    private val appUpdateLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult(),
        ) { result ->
            if (result.resultCode != RESULT_OK) {
                mainViewModel.onAppUpdateRequest(result.resultCode)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        hideSystemNavigationBars()

        pendingDeepLink = intent.data
        val firstStart =
            mainViewModel.onAppStart(
                is24HourFormat = DateFormat.is24HourFormat(this),
                appUpdateLauncher = appUpdateLauncher,
            )
        if (firstStart) {
            // Play Age Signals 0.0.4 requires an Activity for the sharing-access prompt.
            userSession.resolveAudienceContext(this)
        }
        collectToasts()

        setContent {
            ClearEdgeToEdgeProtectionsEffect()

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
                        .fillMaxSize(),
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
                        navController = navController,
                    )
                }

                if (state.permissionDialog) {
                    PopupContainer(
                        onDismiss = { mainViewModel.dismissPermissionDialog() },
                    ) {
                        NotificationsPopup(
                            hasPermission = hasNotificationPermission(),
                            eventReminderMinutes = mainViewModel.eventReminderMinutes(),
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
     * Shows [ToastManager] messages pushed by ViewModels. Lives on the Activity so the
     * toast always uses the current (non-destroyed) Activity context.
     */
    private fun collectToasts() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                toastManager.messages.collect { toast ->
                    if (toast != null) {
                        Toast
                            .makeText(this@MainActivity, toast.resolve(this@MainActivity), toast.duration)
                            .show()
                        toastManager.clear()
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
        args: Bundle?,
    ) {
        mainViewModel.onDestinationChanged(navDestination, args)
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
        val permission =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                android.Manifest.permission.POST_NOTIFICATIONS
            } else {
                return
            }
        if (!hasPermission(permission)) {
            mainViewModel.onPermissionRequest()
            requestPermissionLauncher.launch(permission)
        }
    }

    private fun hasNotificationPermission(): Boolean =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            hasPermission(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            true
        }

    /**
     * Runtime grants needed for the Wi-Fi join path on this API level.
     * On API 29+, [android.provider.Settings.ACTION_WIFI_ADD_NETWORKS] and
     * [android.net.wifi.WifiManager.addNetworkSuggestions] do not require location.
     * On API 28 and below, fine location is required to read configured networks.
     */
    fun hasWirelessPermissions(): Boolean = requiredWirelessPermissions().all { hasPermission(it) }

    fun requestWirelessPermissions() {
        val missing = requiredWirelessPermissions().filterNot { hasPermission(it) }
        if (missing.isNotEmpty()) {
            requestWirelessPermissionsLauncher.launch(missing.toTypedArray())
        }
    }

    private fun requiredWirelessPermissions(): Array<String> =
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            emptyArray()
        }

    /**
     * Returns true if the permission is granted.
     */
    private fun hasPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            this,
            permission,
        ) == PackageManager.PERMISSION_GRANTED

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

    private fun navigateDeepLink(
        controller: NavHostController,
        uri: Uri,
    ) {
        lifecycleScope.launch {
            try {
                val destination = getDestination(uri) ?: return@launch
                if (destination is Navigation.Document) {
                    val code = uri.getQueryParameter("c")
                    if (code.isNullOrBlank()) {
                        Timber.e("Document deep link missing conference code: $uri")
                        Toast
                            .makeText(
                                this@MainActivity,
                                "Could not open document",
                                Toast.LENGTH_SHORT,
                            ).show()
                        return@launch
                    }
                    if (!mainViewModel.switchConferenceByCode(code)) {
                        Timber.e("Unknown conference for document deep link: $code")
                        Toast
                            .makeText(
                                this@MainActivity,
                                "Could not open document",
                                Toast.LENGTH_SHORT,
                            ).show()
                        return@launch
                    }
                }
                controller.navigateTo(destination)

                analytics.logEvent(
                    "open_deep_link",
                    Bundle().apply {
                        putString("uri", uri.toString())
                    },
                )
            } catch (ex: Exception) {
                Timber.e(ex, "Could not navigate to deep link: $uri")
                Toast
                    .makeText(this@MainActivity, "Could not open link", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    internal companion object {
        fun getDestination(uri: Uri): Navigation? =
            DeepLinkParser.parse(
                pathSegment = uri.lastPathSegment,
                conference = uri.getQueryParameter("c"),
                event = uri.getQueryParameter("e"),
                documentId = uri.getQueryParameter("id"),
            )
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

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemNavigationBars()
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

    /**
     * Hides the system navigation bar (back / home / recent). Swiping from the bottom edge
     * temporarily reveals it ([WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE]).
     */
    private fun hideSystemNavigationBars() {
        val controller = WindowCompat.getInsetsController(window, window.decorView)
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller.hide(WindowInsetsCompat.Type.navigationBars())
    }
}
