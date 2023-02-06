package com.advice.schedule.ui.information.vendors

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.advice.schedule.Response
import com.advice.schedule.models.local.Vendor
import com.shortstack.hackertracker.R
import com.shortstack.hackertracker.databinding.FragmentRecyclerviewBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class VendorsFragment : Fragment() {

    private val viewModel by sharedViewModel<VendorsViewModel>()

    private var _binding: FragmentRecyclerviewBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: VendorsAdapter

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

        adapter = VendorsAdapter {
            val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(it.link))
            binding.root.context.startActivity(intent)
        }

        binding.list.adapter = adapter
        binding.toolbar.title = getString(R.string.partners_vendors)
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressed()
        }

        viewModel.getVendors().observe(viewLifecycleOwner) {
            onResource(it)
        }
    }

    private fun onResource(resource: Response<List<Vendor>>) {
        when (resource) {
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

                if (resource.data.isNotEmpty()) {
                    adapter.submitList(resource.data)
                    hideViews()
                } else {
                    adapter.submitList(emptyList())
                    showEmptyView()
                }
            }
            is Response.Error -> {
                setProgressIndicator(active = false)
                showErrorView(resource.exception.message)
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
        binding.emptyView.showError("Vendors not found")
    }

    private fun showErrorView(message: String?) {
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.showError(message)
    }

    private fun hideViews() {
        binding.emptyView.visibility = View.GONE
    }

    companion object {
        fun newInstance() = VendorsFragment()
    }
}



