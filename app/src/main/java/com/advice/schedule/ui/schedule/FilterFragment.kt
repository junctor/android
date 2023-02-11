package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.schedule.PreferenceViewModel
import com.advice.schedule.dObj
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.firebase.FirebaseTagType
import com.advice.schedule.ui.HackerTrackerViewModel
import com.advice.ui.FilterScreenView
import com.advice.ui.UiState
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterFragment : Fragment() {

    private val preferenceViewModel by sharedViewModel<PreferenceViewModel>()
    private val viewModel by sharedViewModel<HackerTrackerViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.tags.observeAsState()
                val tags = (state.value?.dObj as? List<FirebaseTagType>)?.flatMap { it.tags } ?: emptyList()
                FilterScreenView(UiState(tags))
            }
        }
    }
}