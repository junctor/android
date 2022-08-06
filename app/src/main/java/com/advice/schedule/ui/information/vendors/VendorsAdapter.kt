package com.advice.schedule.ui.information.vendors

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.advice.schedule.models.local.Vendor

class VendorsAdapter(private val onClickListener: (Vendor) -> Unit) : ListAdapter<Vendor, VendorViewHolder>(DIFF_UTILS) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VendorViewHolder {
        return VendorViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: VendorViewHolder, position: Int) {
        holder.render(getItem(position), onClickListener)
    }

    companion object {
        val DIFF_UTILS = object : DiffUtil.ItemCallback<Vendor>() {
            override fun areItemsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Vendor, newItem: Vendor): Boolean {
                return oldItem == newItem
            }
        }
    }
}