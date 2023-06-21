package com.example.quitnow.ui.trophies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quitnow.databinding.MtItemHeaderBinding
import com.example.quitnow.databinding.MtItemImageBinding

class AdapterTrophies(private val items: List<Trophy>) : RecyclerView.Adapter<AdapterTrophies.ViewHolder>() {

    abstract class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: Trophy)
    }

    class ItemViewHolder(val binding: MtItemImageBinding) : ViewHolder(binding.root) {
        override fun bind(item: Trophy) {
            if (item is Trophy.Data) {
                binding.root.setImageResource(item.imgRes)

                when (item.achieved) {
                    true -> binding.root.alpha = 1f
                    false -> binding.root.alpha = 0.3f
                }
            }
        }
    }

    class SeparatorViewHolder(val binding: MtItemHeaderBinding) : ViewHolder(binding.root) {
        override fun bind(item: Trophy) {
            if (item is Trophy.Separator) {
                binding.tvHeaderTitle.text = binding.root.context.getString(item.titleRes)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            DATA_ITEM -> {
                val binding = MtItemImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            SEPARATOR_ITEM -> {
                val binding = MtItemHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SeparatorViewHolder(binding)
            }
            else -> throw UnknownError("Unknown view type.")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (holder) {
            is ItemViewHolder -> holder.bind(items[position])
            is SeparatorViewHolder -> holder.bind(items[position])
            else -> throw UnknownError("Unknown view type.")
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position]) {
            is Trophy.Data -> DATA_ITEM
            is Trophy.Separator -> SEPARATOR_ITEM
        }
    }

    companion object {
        const val DATA_ITEM = 0
        const val SEPARATOR_ITEM = 1
    }
}