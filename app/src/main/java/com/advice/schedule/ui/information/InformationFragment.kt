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
import com.advice.schedule.ui.information.info.SupportHelplineFragment
import com.advice.schedule.ui.information.info.WiFiFragment
import com.advice.schedule.ui.information.speakers.SpeakersFragment
import com.advice.schedule.ui.information.vendors.VendorsFragment
import com.shortstack.hackertracker.databinding.FragmentInformationBinding
import org.koin.android.ext.android.inject

class InformationFragment : Fragment() {

    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    private val database: DatabaseManager by inject()

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
            binding.codeOfConduct.isVisible = it.conduct != null
            binding.help.isVisible = it.code.contains("DEFCON")
            binding.wifi.isVisible = it.code.contains("DEFCON")
        }

        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.wifi.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(WiFiFragment.newInstance(), hasAnimation = false)
        }

        binding.help.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(SupportHelplineFragment.newInstance(), hasAnimation = false)
        }

        binding.codeOfConduct.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(CodeOfConductFragment.newInstance(), hasAnimation = false)
        }

        binding.faq.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(FAQFragment.newInstance(), hasAnimation = false)
        }

        binding.vendors.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(VendorsFragment.newInstance(), hasAnimation = false)
        }

        binding.speakers.setOnClickListener {
            (requireActivity() as MainActivity).setAboveFragment(SpeakersFragment.newInstance(), hasAnimation = false)
        }
    }

    companion object {
        fun newInstance(): InformationFragment = InformationFragment()
    }
}