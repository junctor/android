package com.advice.schedule.ui.information.speakers

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.advice.core.utils.Response
import com.advice.schedule.hideKeyboard
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.onQueryTextChanged
import com.advice.schedule.ui.activities.MainActivity
import com.advice.ui.screens.SpeakersScreenView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakersFragment : Fragment() {

    private val viewModel by viewModel<SpeakersViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.speakers.observeAsState().value
                SpeakersScreenView(state ?: emptyList())
            }
        }
    }

    companion object {
        fun newInstance() = SpeakersFragment()
    }
}