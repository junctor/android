package com.advice.schedule.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
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
import com.advice.ui.theme.ScheduleTheme
import org.koin.android.ext.android.inject

class InformationFragment : Fragment() {

    private val viewModel by inject<InformationViewModel>()

    private val analytics by inject<Analytics>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // fix for navigation crash
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val conference = viewModel.conference.collectAsState(initial = null).value

                ScheduleTheme {
                    InformationScreenView(
                        hasCodeOfConduct = conference?.conduct != null,
                        hasSupport = conference?.support != null,
                        hasWifi = conference?.code?.contains("DEFCON") == true,
                        {
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
                        }, {
                            requireActivity().onBackPressed()
                        })
                }
            }
        }
    }

    companion object {
        fun newInstance(): InformationFragment = InformationFragment()
    }
}