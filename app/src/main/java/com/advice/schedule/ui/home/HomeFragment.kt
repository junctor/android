package com.advice.schedule.ui.home

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.advice.core.local.Conference
import com.advice.ui.EventScreenView
import com.advice.ui.HomeScreenView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentHomeBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class HomeFragment : Fragment() {

    private val viewModel by viewModel<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                HomeScreenView()
            }
        }
    }

    private fun showConferenceChooseDialog(conference: Conference, conferences: List<Conference>) {
        val selected = conferences.indexOf(conference)

        val items = conferences.map { it.name }.toTypedArray()

        AlertDialog.Builder(requireContext(), R.style.MyAlertDialogStyle)
            .setTitle(getString(R.string.choose_conference))
            .setSingleChoiceItems(items, selected) { dialog, which ->
                viewModel.changeConference(conferences[which])
                dialog.dismiss()
            }.show()
    }

    companion object {
        fun newInstance() = HomeFragment()
    }
}
