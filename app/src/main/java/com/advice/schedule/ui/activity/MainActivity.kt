package com.advice.schedule.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.core.utils.Storage
import com.advice.play.AppManager
import com.advice.schedule.ui.navigation.NavHost
import com.advice.schedule.ui.navigation.Navigation
import com.advice.schedule.ui.navigation.navigate
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shortstack.hackertracker.BuildConfig
import java.util.jar.Manifest
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import timber.log.Timber

class MainActivity : AppCompatActivity(), KoinComponent {

    private val appManager by inject<AppManager>()
    private val storage by inject<Storage>()
    private val REQUEST_CODE_UPDATE = 9003

    // todo: fix this - this is a hack to get the navController to work
    private lateinit var navController: NavController

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                Timber.d("Permission granted")
            } else {
                Timber.d("Permission denied")
            }
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

            ScheduleTheme {
                NavHost(navController as NavHostController)
            }
        }

        // Only showing the prompt once per version.
        if (storage.updateVersion != BuildConfig.VERSION_CODE) {
            appManager.checkForUpdate(this, REQUEST_CODE_UPDATE)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
                }


                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.data != null) {
            val uri: Uri? = intent.data
            Timber.e("onNewIntent: $uri")
            val conference = uri?.getQueryParameter("c") ?: return
            val event = uri.getQueryParameter("e") ?: return
            navController.navigate(Navigation.Event(conference, event, ""))
        }
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
