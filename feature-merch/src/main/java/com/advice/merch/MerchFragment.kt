package com.advice.merch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.Navigation
import com.advice.merch.screens.MerchScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MerchFragment : Fragment() {

    private val viewModel by sharedViewModel<MerchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            isTransitionGroup = true

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.state.collectAsState(initial = null).value
                ScheduleTheme {
                    if (state != null) {
                        MerchScreenView(
                            state,
                            onSummaryClicked = {
                                (requireActivity() as Navigation).showMerchSummary()
                            }, onMerchClicked = {
                                (requireActivity() as Navigation).showMerchItem(it)
                            })
                    }
                }
            }
        }
    }

    companion object {
        fun newInstance(): MerchFragment {
            val args = Bundle()

            val fragment = MerchFragment()
            fragment.arguments = args
            return fragment
        }
    }
}