package com.example.quitnow.ui.store

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quitnow.R
import com.example.quitnow.data.db.store.StoreItemEntity
import com.example.quitnow.databinding.ItemStoreBinding
import com.example.quitnow.ui.settings.SettingsFragment
import com.example.quitnow.util.hide
import com.example.quitnow.util.show

class AdapterStoreItems(
    private val onDeleteClick: (item: StoreItemEntity) -> Unit,
    private val onPurchaseClick: (item: StoreItemEntity) -> Unit
) : RecyclerView.Adapter<AdapterStoreItems.ViewHolder>() {

    private var items: List<StoreItemEntity> = emptyList()
    private var money: Float = 0f

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<StoreItemEntity>) {
        this.items = items
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMoney(money: Float) {
        this.money = money
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.binding.imgDelete.setOnClickListener {
            onDeleteClick.invoke(item)
        }

        holder.binding.tvPurchase.setOnClickListener {
            onPurchaseClick.invoke(item)
        }

        holder.bind(item, money)
    }

    override fun getItemCount() = items.size

    class ViewHolder(val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(item: StoreItemEntity, money: Float) {
            val context = binding.root.context
            binding.tvTitle.text = item.title
            binding.tvPrice.text = item.price.toString() + SettingsFragment.CURRENCY

            when (item.bought) {
                true -> {
                    binding.wrapper.setBackgroundColor(ContextCompat.getColor(context, R.color.lightGreen))
                    binding.pbStatus.progress = 100
                    binding.tvPurchase.text = context.getString(R.string.store_bought)
                    binding.pbStatus.hide()
                }
                false -> {
                    binding.wrapper.setBackgroundColor(0)
                    val percent = money * 100 / item.price
                    when {
                        percent >= 100 -> binding.pbStatus.progress = 100
                        else -> binding.pbStatus.progress = percent.toInt()
                    }
                    binding.tvPurchase.text = context.getString(R.string.store_purchase)
                    binding.pbStatus.show()
                }
            }
        }
    }
}