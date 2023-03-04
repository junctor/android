package com.advice.schedule.ui.information.vendors

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.screens.VendorsScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VendorsFragment : Fragment() {

    private val viewModel by sharedViewModel<VendorsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    val vendors = viewModel.vendors.collectAsState(initial = null).value
                    VendorsScreenView(vendors = vendors ?: emptyList()) {
                        requireActivity().onBackPressed()
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance() = VendorsFragment()
    }
}



