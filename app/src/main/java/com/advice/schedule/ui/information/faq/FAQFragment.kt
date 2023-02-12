package com.advice.schedule.ui.information.faq

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.advice.core.utils.Response
import com.advice.schedule.hideKeyboard
import com.advice.schedule.models.local.FAQAnswer
import com.advice.schedule.models.local.FAQQuestion
import com.advice.schedule.onQueryTextChanged
import com.advice.ui.screens.FAQScreenView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
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
