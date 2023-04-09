package com.advice.schedule.ui.merch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.screens.MerchSummaryScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MerchSummaryFragment : Fragment() {

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
                    MerchSummaryScreenView(state ?: emptyList(), {
                        viewModel.removeMerch(it)
                    }, {
                        viewModel.addMerch(it)
                    }, {
                        requireActivity().onBackPressed()
                    })
                }
            }
        }
    }

    companion object {
        fun newInstance(): MerchSummaryFragment {
            val args = Bundle()

            val fragment = MerchSummaryFragment()
            fragment.arguments = args
            return fragment
        }
    }
}