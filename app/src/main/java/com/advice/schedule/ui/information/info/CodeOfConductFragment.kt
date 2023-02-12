package com.advice.schedule.ui.information.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.screens.CodeOfConductScreenView
import org.koin.androidx.viewmodel.ext.android.viewModel

class CodeOfConductFragment : Fragment() {

    private val viewModel by viewModel<ConferenceViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.conference.collectAsState(initial = null)
                CodeOfConductScreenView(state.value?.conduct) {
                    requireActivity().onBackPressed()
                }
            }
        }
    }

    companion object {
        fun newInstance() = CodeOfConductFragment()
    }
}