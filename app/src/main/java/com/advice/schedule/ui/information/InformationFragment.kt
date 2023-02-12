package com.advice.schedule.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.advice.schedule.database.DatabaseManager
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.ui.information.faq.FAQFragment
import com.advice.schedule.ui.information.info.CodeOfConductFragment
import com.advice.schedule.ui.information.info.SupportFragment
import com.advice.schedule.ui.information.info.WiFiFragment
import com.advice.schedule.ui.information.locations.LocationsFragment
import com.advice.schedule.ui.information.speakers.SpeakersFragment
import com.advice.schedule.ui.information.vendors.VendorsFragment
import com.advice.schedule.utilities.Analytics
import com.advice.ui.screens.InformationScreenView
import com.shortstack.hackertracker.databinding.FragmentInformationBinding
import org.koin.android.ext.android.inject

class InformationFragment : Fragment() {

    private val database by inject<DatabaseManager>()
    private val analytics by inject<Analytics>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = database.conference.observeAsState().value
                if (state != null) {
                    InformationScreenView(
                        hasCodeOfConduct = state.conduct != null,
                        hasSupport = state.support != null,
                        hasWifi = state.code.contains("DEFCON")
                    ) {
                        when (it) {
                            -1 -> {
                                (requireActivity() as MainActivity).setAboveFragment(WiFiFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_WIFI)
                            }
                            -2 -> {
                                (requireActivity() as MainActivity).setAboveFragment(CodeOfConductFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_CODE_OF_CONDUCT)
                            }
                            -3 -> {
                                (requireActivity() as MainActivity).setAboveFragment(SupportFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_HELP_AND_SUPPORT)
                            }
                            1 -> {
                                (requireActivity() as MainActivity).setAboveFragment(LocationsFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_LOCATIONS)
                            }
                            0 -> {
                                (requireActivity() as MainActivity).setAboveFragment(FAQFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_FAQ)
                            }
                            3 -> {
                                (requireActivity() as MainActivity).setAboveFragment(VendorsFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_VENDORS_AND_PARTNERS)
                            }
                            2 -> {
                                (requireActivity() as MainActivity).setAboveFragment(SpeakersFragment.newInstance(), hasAnimation = false)
                                analytics.setScreen(Analytics.SCREEN_SPEAKERS)
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): InformationFragment = InformationFragment()
    }
}