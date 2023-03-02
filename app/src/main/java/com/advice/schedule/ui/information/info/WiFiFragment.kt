package com.advice.schedule.ui.information.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.screens.WifiScreenView
import com.advice.ui.theme.ScheduleTheme

class WiFiFragment : Fragment() {
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    WifiScreenView {
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = WiFiFragment()
    }
}