package com.advice.schedule.ui.information.speakers

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.advice.core.utils.Response
import com.advice.schedule.hideKeyboard
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.onQueryTextChanged
import com.advice.schedule.ui.activities.MainActivity
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class SpeakersFragment : Fragment() {

    private val viewModel by viewModel<SpeakersViewModel>()

    private var _binding: FragmentRecyclerviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView

    private lateinit var adapter: SpeakersAdapter

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu, menu)

        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.onQueryTextChanged {
            viewModel.setSearchQuery(it)
        }

        searchView.setOnQueryTextFocusChangeListener { _, isFocused ->
            if (!isFocused && searchView.query.isNullOrBlank()) {
                searchItem.collapseActionView()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecyclerviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        adapter = SpeakersAdapter {
            (requireActivity() as MainActivity).showSpeaker(it)
        }

        binding.toolbar.title = getString(R.string.speakers)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.list.adapter = adapter
        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    requireActivity().hideKeyboard()
                }
            }
        })

        viewModel.getSpeakers().observe(viewLifecycleOwner) { response ->
            onResponse(response)
        }

        setHasOptionsMenu(true)
    }

    private fun onResponse(response: Response<List<Speaker>>) {
        when (response) {
            is Response.Init -> {
                setProgressIndicator(active = false)
                showInitView()
            }
            is Response.Loading -> {
                setProgressIndicator(active = true)
                adapter.submitList(emptyList())
                hideViews()
            }
            is Response.Success -> {
                setProgressIndicator(active = false)

                if (response.data.isNotEmpty()) {
                    adapter.submitList(response.data)
                    hideViews()
                } else {
                    adapter.submitList(emptyList())
                    showEmptyView()
                }
            }
            is Response.Error -> {
                setProgressIndicator(active = false)
                showErrorView(response.exception.message)
            }
        }
    }

    private fun setProgressIndicator(active: Boolean) {
        binding.loadingProgress.visibility = if (active) View.VISIBLE else View.GONE
    }

    private fun showInitView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showDefault()
    }

    private fun showEmptyView() {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError("Speakers not found")
    }

    private fun showErrorView(message: String?) {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError(message)
    }

    private fun hideViews() {
        binding.emptyView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = SpeakersFragment()
    }
}