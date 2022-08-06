package com.advice.schedule.ui.information.locations

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.advice.schedule.models.local.LocationContainer

class LocationsAdapter(private val onClickListener: (LocationContainer) -> Unit) : ListAdapter<LocationContainer, LocationViewHolder>(DIFF_UTILS) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.render(getItem(position), onClickListener)
    }

    override fun submitList(list: List<LocationContainer>?) {
        super.submitList(list?.filter { it.isExpanded })
    }

    companion object {
        private val DIFF_UTILS = object : DiffUtil.ItemCallback<LocationContainer>() {
            override fun areItemsTheSame(oldItem: LocationContainer, newItem: LocationContainer): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: LocationContainer, newItem: LocationContainer): Boolean {
                return oldItem.status == newItem.status
                        && oldItem.title == newItem.title
                        && oldItem.isChildrenExpanded && newItem.isChildrenExpanded
                        && oldItem.isExpanded == newItem.isExpanded
                        && oldItem.hasChildren == newItem.hasChildren
            }
        }
    }
}