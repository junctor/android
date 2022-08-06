package com.advice.schedule.ui.information.vendors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.Vendor
import com.shortstack.hackertracker.databinding.RowVendorBinding

class VendorViewHolder(private val binding: RowVendorBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun render(vendor: Vendor, onClickListener: (Vendor) -> Unit) {
        binding.title.text = vendor.name
        binding.description.text = vendor.summary

        binding.link.visibility = if (vendor.link.isNullOrBlank()) {
            View.GONE
        } else {
            View.VISIBLE
        }

        binding.link.setOnClickListener {
            onClickListener(vendor)
        }
    }

    companion object {

        fun inflate(parent: ViewGroup): VendorViewHolder {
            val binding = RowVendorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return VendorViewHolder(binding)
        }
    }
}
