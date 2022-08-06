package com.advice.schedule.ui.information.speakers

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.advice.schedule.models.local.Speaker

class SpeakersAdapter(private val onClickListener: (Speaker) -> Unit) : ListAdapter<Speaker, SpeakerViewHolder>(DIFF_UTILS) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpeakerViewHolder {
        return SpeakerViewHolder.inflate(parent)
    }

    override fun onBindViewHolder(holder: SpeakerViewHolder, position: Int) {
        holder.render(getItem(position), onClickListener)
    }

    companion object {
        val DIFF_UTILS = object : DiffUtil.ItemCallback<Speaker>() {
            override fun areItemsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Speaker, newItem: Speaker): Boolean {
                return oldItem == newItem
            }
        }
    }
}