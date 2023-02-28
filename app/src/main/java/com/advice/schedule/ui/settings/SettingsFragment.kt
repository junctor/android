package com.advice.schedule.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.screens.SettingScreenView
import com.advice.ui.theme.ScheduleTheme
import com.shortstack.hackertracker.BuildConfig
import org.koin.core.KoinComponent
import org.koin.core.inject


class SettingsFragment : Fragment(), KoinComponent {

    private val viewModel by inject<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            // fix for navigation crash
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val conference = viewModel.getConference().observeAsState().value
                    SettingScreenView(conference?.timezone ?: "---", BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE) {
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    private fun openDeveloperTwitter(isFollow: Boolean = false) {
        try {
            val url = if (isFollow) {
                "https://twitter.com/intent/user?screen_name=_advice_dog"
            } else {
                "https://twitter.com/_advice_dog"
            }
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(url))
            context?.startActivity(intent)
            viewModel.onDeveloperTwitterClick(isFollow)
        } catch (ex: Exception) {
            // do nothing
        }
    }


    companion object {
        fun newInstance() = SettingsFragment()
    }
}
