package com.advice.merch

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.local.Merch
import com.advice.merch.screens.MerchItemScreenView
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MerchItemFragment : Fragment() {

    private val viewModel by sharedViewModel<MerchViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val merch = arguments?.getParcelable<Merch>("merch") ?: error("merch cannot be null")

        return ComposeView(requireContext()).apply {
            isTransitionGroup = true

            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ScheduleTheme {
                    MerchItemScreenView(
                        merch,
                        onAddClicked = {
                            viewModel.addToCart(it)
                            dismiss()
                        }) {
                        dismiss()
                    }
                }
            }
        }
    }

    private fun dismiss() {
        requireActivity().onBackPressed()
    }

    companion object {
        fun newInstance(merch: Merch): MerchItemFragment {
            val args = Bundle()
            args.putParcelable("merch", merch)

            val fragment = MerchItemFragment()
            fragment.arguments = args
            return fragment
        }
    }
}