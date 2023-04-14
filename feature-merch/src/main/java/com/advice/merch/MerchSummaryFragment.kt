package com.advice.merch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.merch.screens.MerchSummaryScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

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
                val state = viewModel.summary.collectAsState(initial = null).value
                ScheduleTheme {
                    if (state != null) {
                        MerchSummaryScreenView(state, { id, quantity, selectedOption ->
                            viewModel.setQuantity(id, quantity, selectedOption)
                        }, {
                            requireActivity().onBackPressed()
                        }, {
                            viewModel.applyDiscount(it)
                        })
                    }
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