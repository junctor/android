package com.advice.schedule.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.shortstack.hackertracker.databinding.FragmentInformationBinding
import org.koin.android.ext.android.inject

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val database by inject<DatabaseManager>()
    private val analytics by inject<Analytics>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        database.conference.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.codeOfConduct.isVisible = it.conduct != null
                binding.support.isVisible = it.support != null
                binding.wifi.isVisible = it.code.contains("DEFCON")
            }
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.wifi.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(WiFiFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_WIFI)
        }

        binding.codeOfConduct.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(CodeOfConductFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_CODE_OF_CONDUCT)
        }

        binding.support.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(SupportFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_HELP_AND_SUPPORT)
        }

        binding.locations.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(LocationsFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_LOCATIONS)
        }

        binding.faq.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(FAQFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_FAQ)
        }

        binding.vendors.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(VendorsFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_VENDORS_AND_PARTNERS)
        }

        binding.speakers.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(SpeakersFragment.newInstance(), hasAnimation = false)
            analytics.setScreen(Analytics.SCREEN_SPEAKERS)
        }
    }

    companion object {
        fun newInstance(): InformationFragment = InformationFragment()
    }
}