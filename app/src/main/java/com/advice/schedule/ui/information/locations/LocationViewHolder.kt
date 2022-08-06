package com.advice.schedule.ui.information.locations

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.advice.schedule.models.local.LocationContainer
import com.shortstack.hackertracker.databinding.LocationRowBinding

class LocationViewHolder(private val binding: LocationRowBinding) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun inflate(parent: ViewGroup): LocationViewHolder {
            val binding = LocationRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return LocationViewHolder(binding)
        }
    }

    fun render(container: LocationContainer, onClickListener: (LocationContainer) -> Unit) = with(binding) {
        expand.isVisible = container.hasChildren
        expand.rotation = if (container.isChildrenExpanded) {
            0f
        } else {
            90f
        }

        location.setLocation(container, useShortLabel = true)

        root.setOnClickListener {
            val target = if (container.isChildrenExpanded) {
                90f
            } else {
                0f
            }
            expand.animate().rotation(target).start()
            onClickListener(container)
        }
    }
}