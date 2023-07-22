package com.advice.schedule.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.advice.schedule.ui.navigation.NavHost
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.core.component.KoinComponent
import timber.log.Timber

class MainActivity :
    AppCompatActivity(),
    KoinComponent {

    private lateinit var navController: NavController

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
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null && intent.data != null) {
            val uri: Uri? = intent.data
            val conference = uri?.getQueryParameter("c")
            val event = uri?.getQueryParameter("e")
            Timber.e("onNewIntent: $uri")
            navController.navigate("event/$event")
        }
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
