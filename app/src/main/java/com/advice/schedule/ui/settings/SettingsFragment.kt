package com.advice.schedule.ui.settings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.advice.schedule.models.local.Conference
import com.advice.schedule.ui.themes.ThemesManager
import com.advice.schedule.utilities.Storage
import com.shortstack.hackertracker.BuildConfig
import com.shortstack.hackertracker.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*


class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel by viewModel<SettingsViewModel>()

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)

        screen.apply {
            // Timezone
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_time_zone)
                summaryOn = getString(R.string.setting_time_zone_summary_on)
                summaryOff = getString(R.string.setting_time_zone_summary_off)
                key = Storage.FORCE_TIME_ZONE_KEY
            })

            // Showing filter button
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_filter_button_shown)
                key = Storage.FILTER_BUTTON_SHOWN
                setDefaultValue(true)
            })

            // Easter Eggs
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_easter_eggs)
                summary = getString(R.string.setting_easter_eggs_summary)
                key = Storage.EASTER_EGGS_ENABLED_KEY
            })

            // Analytics
            addPreference(SwitchPreference(context).apply {
                title = getString(R.string.setting_user_analytics)
                key = Storage.USER_ANALYTICS_KEY
            })

            if (viewModel.hasReboot().value == true) {
                // Safe Mode
                addPreference(Preference(context).apply {
                    title = getString(R.string.settings_reboot)
                    summary = getString(R.string.settings_safe_mode_summary)
                    key = SettingsFragment.SAFE_MODE_KEY
                    setOnPreferenceClickListener {
                        viewModel.setTheme(ThemesManager.Theme.SafeMode)
                        requireActivity().recreate()
                        true
                    }
                })
            }
        }

        preferenceScreen = screen
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // todo:
        view.findViewById<Toolbar>(R.id.toolbar).setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.getConference().observe(viewLifecycleOwner) {
            if (it != null) {
                updateConference(it)
                updateTimezonePreference(it)
            }
        }

        val versionTextView = view.findViewById<TextView>(R.id.version)
        versionTextView.text =
            getString(R.string.version, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE)

        versionTextView.setOnClickListener {
            viewModel.onVersionClick()
        }

        viewModel.shouldDisplayUsername().observe(viewLifecycleOwner) { id ->
            if (id != null) {
                versionTextView.text = id
                versionTextView.setOnLongClickListener {
                    val clipboard = context?.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager?
                    val clip = ClipData.newPlainText("user", id)
                    clipboard?.setPrimaryClip(clip)
                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                    true
                }
            }
        }

        view.findViewById<View>(R.id.twitter_card).setOnClickListener {
            openDeveloperTwitter(isFollow = false)
        }

        view.findViewById<TextView>(R.id.twitter_action).setOnClickListener {
            openDeveloperTwitter(isFollow = true)
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

    private fun updateConference(conference: Conference) {
        val preference = preferenceScreen.findPreference<Preference>(CHANGE_CONFERENCE_KEY)
        preference?.summary = conference.name
    }

    private fun updateTimezonePreference(conference: Conference) {
        val preference =
            preferenceScreen.findPreference<SwitchPreference>(Storage.FORCE_TIME_ZONE_KEY)
        preference?.title = getString(
            R.string.setting_time_zone,
            conference.timezone.uppercase(Locale.getDefault())
        )
    }

    companion object {
        fun newInstance() = SettingsFragment()
        private const val CHANGE_CONFERENCE_KEY = "change_conference"
        private const val SAFE_MODE_KEY = "safe_mode"
    }
}
