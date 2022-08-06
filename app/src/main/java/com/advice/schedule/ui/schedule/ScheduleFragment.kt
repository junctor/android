package com.advice.schedule.ui.schedule

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.Response
import com.advice.schedule.hideKeyboard
import com.advice.schedule.models.Day
import com.advice.schedule.models.firebase.FirebaseTag
import com.advice.schedule.models.local.Event
import com.advice.schedule.models.local.Location
import com.advice.schedule.models.local.Speaker
import com.advice.schedule.onQueryTextChanged
import com.advice.schedule.ui.PanelsFragment
import com.advice.schedule.ui.activities.MainActivity
import com.advice.schedule.ui.schedule.list.ScheduleAdapter
import com.advice.schedule.utilities.Storage
import com.advice.schedule.views.DaySelectorView
import com.advice.timehop.StickyRecyclerHeadersDecoration
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentScheduleBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.util.*
import kotlin.math.abs


class ScheduleFragment : Fragment(), KoinComponent {

    private val storage by inject<Storage>()

    private val viewModel by sharedViewModel<ScheduleViewModel>()

    private var _binding: FragmentScheduleBinding? = null
    private val binding get() = _binding!!

    private lateinit var searchView: SearchView

    private val adapter = ScheduleAdapter()
    private val decoration = StickyRecyclerHeadersDecoration(adapter)

    private var shouldScroll = true

    private var forceTimeZone = storage.forceTimeZone

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
        _binding = FragmentScheduleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)

        val type = arguments?.getParcelable<FirebaseTag>(EXTRA_TYPE)
        val location = arguments?.getParcelable<Location>(EXTRA_LOCATION)
        val speaker = arguments?.getParcelable<Speaker>(EXTRA_SPEAKER)

        if (type != null || location != null || speaker != null) {
            binding.toolbar.title = type?.label ?: location?.shortName ?: location?.name ?: speaker?.name
            binding.title.visibility = View.GONE
            binding.filter.visibility = View.GONE
            binding.toolbar.navigationIcon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_arrow_back_ios_new_24
            )
        }

        shouldScroll = true
        binding.list.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            if (parentFragment != null) {
                (parentFragment as PanelsFragment).openStartPanel()
            } else {
                requireActivity().onBackPressed()
            }
        }

        binding.filter.setOnClickListener {
            (parentFragment as PanelsFragment).openEndPanel()
        }

        binding.list.addItemDecoration(decoration)

        binding.list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val manager = binding.list.layoutManager as? LinearLayoutManager
                if (manager != null) {
                    val first = manager.findFirstVisibleItemPosition()
                    val last = manager.findLastVisibleItemPosition()

                    if (first == -1 || last == -1)
                        return

                    binding.daySelector.onScroll(
                        adapter.getDateOfPosition(first),
                        adapter.getDateOfPosition(last)
                    )
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    requireActivity().hideKeyboard()
                }
            }
        })

        binding.daySelector.addOnDaySelectedListener(object :
            DaySelectorView.OnDaySelectedListener {
            override fun onDaySelected(day: Date) {
                scrollToDate(day)
            }
        })

        val liveData = when {
            type != null -> {
                viewModel.getSchedule(type)
            }
            location != null -> {
                viewModel.getSchedule(location)
            }
            speaker != null -> {
                viewModel.getSchedule(speaker)
            }
            else -> {
                viewModel.getSchedule()
            }
        }

        liveData.observe(viewLifecycleOwner) { response ->
            hideViews()

            when (response) {
                Response.Init -> {
                    showEmptyView()
                }
                Response.Loading -> {
                    adapter.clearAndNotify()
                    showProgress()
                }
                is Response.Success -> {
                    val list = adapter.setSchedule(response.data)
                    val days = list.filterIsInstance<Day>()

                    binding.daySelector.setDays(days)

                    if (list.isEmpty()) {
                        showEmptyView()
                    }

                    scrollToCurrentPosition(list)
                }
                is Response.Error -> {
                    showErrorView(response.exception.message)
                }
            }
        }

        updateFABState()

        setHasOptionsMenu(true)
    }

    fun invalidate() {
        // temp fix for crash when binding is null
        if (_binding == null) {
            return
        }

        updateFABState()

        if (forceTimeZone == storage.forceTimeZone) {
            return
        }
        forceTimeZone = storage.forceTimeZone

        adapter.refresh()
        decoration.invalidateHeaders()
    }

    private fun updateFABState() {
        if (storage.fabShown) {
            binding.filter.show()
        } else {
            binding.filter.hide()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> (context as MainActivity).showSearch()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun scrollToCurrentPosition(data: ArrayList<Any>) {
        val manager = binding.list.layoutManager ?: return
        val first = data.filterIsInstance<Event>().firstOrNull { !it.hasStarted } ?: return

        if (shouldScroll) {
            shouldScroll = false
            val index = getScrollIndex(data, first)
            manager.scrollToPosition(index)
        }
    }

    private fun getScrollIndex(data: ArrayList<Any>, first: Event): Int {
        val event = data.indexOf(first)

        val element = data.subList(0, event).filterIsInstance<Day>().last()
        val index = data.indexOf(element)

        val x = data.subList(index, event).filterIsInstance<Event>()
            .firstOrNull { it.start.toDate().time != first.start.toDate().time } == null
        if (!x) {
            return event
        }


        if (index != -1) {
            return index
        }
        return event
    }

    private fun scrollToDate(date: Date) {
        val layoutManager = binding.list.layoutManager as LinearLayoutManager

        val current = layoutManager.findFirstVisibleItemPosition()
        val index = adapter.getDatePosition(date)
        if (index == -1) {
            Timber.e("Could not find Date within the list: $date")
            return
        }

        if (abs(current - index) > QUICK_SCROLL_MIN_DISTANCE) {
            val target = if (index > current) {
                index - QUICK_SCROLL_NEARBY_DISTANCE
            } else {
                index + QUICK_SCROLL_NEARBY_DISTANCE
            }
            layoutManager.scrollToPositionWithOffset(target, 0)
        }

        binding.list.postDelayed({
            val scroller = object : LinearSmoothScroller(context) {

                override fun getVerticalSnapPreference(): Int {
                    return SNAP_TO_START
                }
            }
            scroller.targetPosition = index
            layoutManager.startSmoothScroll(scroller)
        }, QUICK_SCROLL_SMOOTH_DELAY)
    }

    private fun showProgress() {
        binding.loadingProgress.visibility = View.VISIBLE
    }

    private fun hideViews() {
        binding.empty.visibility = View.GONE
        binding.loadingProgress.visibility = View.GONE
    }

    private fun showEmptyView() {
        binding.empty.visibility = View.VISIBLE
    }

    private fun showErrorView(message: String?) {
        binding.empty.showError(message)
        binding.empty.visibility = View.VISIBLE
    }

    companion object {
        private const val EXTRA_TYPE = "type"
        private const val EXTRA_LOCATION = "location"
        private const val EXTRA_SPEAKER = "speaker"

        private const val QUICK_SCROLL_MIN_DISTANCE = 50
        private const val QUICK_SCROLL_NEARBY_DISTANCE = 30
        private const val QUICK_SCROLL_SMOOTH_DELAY = 20L

        fun newInstance(): ScheduleFragment {
            return ScheduleFragment()
        }

        fun newInstance(type: FirebaseTag): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = bundleOf(
                    EXTRA_TYPE to type
                )
            }
        }

        fun newInstance(location: Location): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = bundleOf(
                    EXTRA_LOCATION to location
                )
            }
        }

        fun newInstance(speaker: Speaker): ScheduleFragment {
            return ScheduleFragment().apply {
                arguments = bundleOf(
                    EXTRA_SPEAKER to speaker
                )
            }
        }
    }
}