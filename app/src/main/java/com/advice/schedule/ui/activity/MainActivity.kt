package com.advice.schedule.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.core.utils.Storage
import com.advice.firebase.extensions.document_cache_reads
import com.advice.firebase.extensions.document_reads
import com.advice.firebase.extensions.listeners_count
import com.advice.play.AppManager
import com.advice.schedule.navigation.Navigation
import com.advice.schedule.navigation.NavigationManager
import com.advice.schedule.navigation.navigate
import com.advice.schedule.navigation.setRoutes
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

    // todo: fix this - this is a hack to get the navController to work
    private lateinit var navController: NavController

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

            ScheduleTheme {
                navigation.setRoutes(this, navController = navController as NavHostController)
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
            route = route.replace("{${it}}", "${args.getString(it)}")
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

    fun requestNotificationPermission() {
        val permission = android.Manifest.permission.POST_NOTIFICATIONS
        if (!hasPermission(permission)) {
            analytics.logEvent(
                "request_permission", bundleOf(
                    "permission" to "POST_NOTIFICATIONS"
                )
            )
            requestPermissionLauncher.launch(permission)
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
            navController.navigate(Navigation.Event(conference, event, ""))
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
