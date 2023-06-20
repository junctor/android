package com.advice.schedule.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.advice.schedule.ui.navigation.NavHost
import com.advice.ui.theme.ScheduleTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import org.koin.core.KoinComponent
import timber.log.Timber

class MainActivity :
    AppCompatActivity(),
    KoinComponent {

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

            ScheduleTheme {
                NavHost()
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val target = when {
            intent == null -> null
            intent.hasExtra("target") -> intent.getLongExtra("target", -1L)
            intent.data != null -> {
                intent.data.toString().substringAfter("events/").take(5).toLongOrNull()
            }

            else -> null
        }

        Timber.d("target: $target")
        if (target != null) {
            //todo: navController.navigate("event/$target")
        }
    }

    fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}
