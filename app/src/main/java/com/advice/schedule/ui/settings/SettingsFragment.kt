package com.advice.schedule.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.advice.core.local.Conference
import com.advice.schedule.ui.themes.ThemesManager
import com.advice.schedule.utilities.Storage
import com.advice.ui.screens.SettingScreenView
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import java.util.*


class SettingsFragment : Fragment(), KoinComponent {

    private val viewModel by inject<SettingsViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val conference = viewModel.getConference().observeAsState().value
                // getString(
                //            R.string.setting_time_zone,
                //            conference.timezone.uppercase(Locale.getDefault())
                //        )
                // getString(R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)
                SettingScreenView()
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
