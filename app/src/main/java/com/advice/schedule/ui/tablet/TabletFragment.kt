package com.advice.schedule.ui.tablet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.ui.tablet.WideHomeScreen
import com.advice.ui.theme.ScheduleTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class TabletFragment : Fragment() {

    private val viewModel by viewModel<TabletViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.state.collectAsState(initial = null).value
                ScheduleTheme {
                    if (state != null) {
                        WideHomeScreen(state.conference, state.articles, state.schedule.groupBy { it.date.time.toString() }, state.tags, {}, {})
                    } else {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }

}