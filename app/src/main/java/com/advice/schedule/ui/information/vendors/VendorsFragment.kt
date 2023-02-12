package com.advice.schedule.ui.information.vendors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.advice.core.utils.Response
import com.advice.schedule.dObj
import com.advice.schedule.models.local.Vendor
import com.advice.ui.screens.VendorsScreenView
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VendorsFragment : Fragment() {

    private val viewModel by sharedViewModel<VendorsViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.getVendors().observeAsState()
                val vendors = state.value?.dObj as? List<Vendor> ?: emptyList()
                VendorsScreenView(vendors = vendors) {
                    requireActivity().onBackPressed()
                }
            }
        }
    }
    companion object {
        fun newInstance() = VendorsFragment()
    }
}



