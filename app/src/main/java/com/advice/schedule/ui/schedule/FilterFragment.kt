package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.ui.FiltersScreenState
import com.advice.ui.screens.FilterScreenView
import org.koin.androidx.viewmodel.ext.android.viewModel

class FilterFragment : Fragment() {

    private val viewModel by viewModel<FiltersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.state.collectAsState(initial = FiltersScreenState.Init).value
                FilterScreenView(state) {
                    viewModel.toggle(it)
                }
            }
        }
    }
}
