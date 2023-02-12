package com.advice.schedule.ui.information.faq

import android.os.Bundle
import android.view.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.screens.FAQScreenView
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


class FAQFragment : Fragment() {

    private val viewModel by sharedViewModel<FAQViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.faqs.collectAsState(initial = emptyList())
                FAQScreenView(faqs = state.value) {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    companion object {
        fun newInstance() = FAQFragment()
    }
}
