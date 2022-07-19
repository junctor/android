package com.advice.schedule.ui

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.advice.schedule.ui.activities.MainActivity
import com.discord.panels.OverlappingPanelsLayout
import com.discord.panels.PanelState
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.PanelsFragmentBinding

class PanelsFragment : Fragment() {

    private var _binding: PanelsFragmentBinding? = null
    private val binding get() = _binding!!

    private var isOpen = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = PanelsFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // force hide the bottom navigation bar
        binding.bottomNavigation.translationY = 300f

        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    binding.overlappingPanels.closePanels()
                    false
                }
                R.id.nav_information -> {
                    showInformation()
                    false
                }
                R.id.nav_map -> {
                    showMap()
                    false
                }

                R.id.nav_settings -> {
                    showSettings()
                    false
                }
                else -> false
            }
        }

        binding.overlappingPanels.registerStartPanelStateListeners(object : OverlappingPanelsLayout.PanelStateListener {
            override fun onPanelStateChange(panelState: PanelState) {
                when (panelState) {
                    PanelState.Opening,
                    PanelState.Opened -> showBottomNavigation()
                    PanelState.Closing,
                    PanelState.Closed -> hideBottomNavigation()
                }
            }
        })
    }

    private fun showInformation() {
        (context as MainActivity).showInformation()
    }

    private fun showMap() {
        (context as MainActivity).showMap()
    }

    private fun showSettings() {
        (context as MainActivity).showSettings()
    }

    private fun hideBottomNavigation() {
        if (!isOpen) {
            return
        }
        isOpen = false
        binding.bottomNavigation.clearAnimation()
        ObjectAnimator.ofFloat(
            binding.bottomNavigation,
            "translationY",
            binding.bottomNavigation.height.toFloat()
        ).apply {
            duration = ANIMATION_DURATION
            start()
        }
    }

    private fun showBottomNavigation() {
        if (isOpen) {
            return
        }
        isOpen = true
        binding.bottomNavigation.clearAnimation()
        ObjectAnimator.ofFloat(binding.bottomNavigation, "translationY", 0f).apply {
            duration = ANIMATION_DURATION
            start()
        }
    }

    fun openStartPanel() {
        binding.overlappingPanels.openStartPanel()
    }

    fun openEndPanel() {
        binding.overlappingPanels.openEndPanel()
    }

    companion object {
        private const val ANIMATION_DURATION = 250L
    }
}