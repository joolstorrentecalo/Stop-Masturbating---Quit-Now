package com.example.quitnow.ui.home.goal

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quitnow.R
import com.example.quitnow.databinding.FragmentGoalListDialogItemBinding

class AdapterGoals(private var items: List<String>,
                   private var selectedIndex: Int,
                   private var onItemSelected: (position: Int) -> Unit) : RecyclerView.Adapter<AdapterGoals.ViewHolder>() {

    data class Selected(val flag: Boolean)

    private fun selectItem(position: Int) {
        val prevPosition = selectedIndex
        selectedIndex = position

        if (prevPosition == position) {
            return
        }

        if (prevPosition > -1) {
            notifyItemChanged(prevPosition, Selected(false))
        }

        if (position > -1) {
            notifyItemChanged(position, Selected(true))
        }

        onItemSelected.invoke(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentGoalListDialogItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.root.setOnClickListener {
            selectItem(position)
        }

        holder.bind(item, selectedIndex == position)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        when (val payload = payloads.firstOrNull()) {
            is Selected -> holder.bindSelected(payload.flag)
            else -> onBindViewHolder(holder, position)
        }
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(val binding: FragmentGoalListDialogItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: String, selected: Boolean) {
            binding.tvTitle.text = item

            bindSelected(selected)
        }

        fun bindSelected(flag: Boolean) {
            when (flag) {
                true -> binding.tvTitle.setBackgroundResource(R.drawable.rounded_primary_background)
                false -> binding.tvTitle.setBackgroundResource(0)
            }
        }
    }
}