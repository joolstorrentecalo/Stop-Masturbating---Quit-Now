package com.example.quitnow.ui.store

import android.annotation.SuppressLint
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quitnow.MainViewModel
import com.example.quitnow.R
import com.example.quitnow.data.db.store.StoreItemEntity
import com.example.quitnow.data.db.user.UserWithStoreItems
import com.example.quitnow.databinding.FragmentStoreBinding
import com.example.quitnow.ui.dialog.AddItemDialogFragment
import com.example.quitnow.ui.settings.SettingsFragment
import com.example.quitnow.util.Epoch
import com.example.quitnow.util.toPx


class StoreFragment : Fragment() {

    private var _binding: FragmentStoreBinding? = null
    private val binding: FragmentStoreBinding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private val adapter = AdapterStoreItems(this::onDeleteItem, this::onPurchaseItem)

    private var currentMoney: Float = 0f

    private val userWithStoreItemsObserver = Observer { userWithStoreItems: UserWithStoreItems? ->
        userWithStoreItems ?: return@Observer
        onUserDataChanged(userWithStoreItems)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentStoreBinding.inflate(inflater, container, false)

        viewModel.userWithStoreItems.observe(viewLifecycleOwner, userWithStoreItemsObserver)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = this@StoreFragment.adapter
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    outRect.top = 5.toPx(context)
                    outRect.bottom = 5.toPx(context)
                    outRect.left = 10.toPx(context)
                    outRect.right = 10.toPx(context)
                }
            })
        }

        binding.fabAddItem.setOnClickListener {
            val dialog = AddItemDialogFragment().also {
                it.onSubmitClick = { title, price ->
                    addItem(title, price)
                }
            }

            dialog.show(childFragmentManager, AddItemDialogFragment.TAG)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setCurrentMoney(money: Float) {
        currentMoney = money
        binding.tvCurrentMoney.text = "$money ${SettingsFragment.CURRENCY}"
    }

    private fun addItem(name: String, price: Float) {
        viewModel.addStoreItem(
            StoreItemEntity(
                id = 0,
                title = name,
                price = price,
                bought = false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    private fun onUserDataChanged(userWithStoreItems: UserWithStoreItems) {
        var moneySaved: Float = Epoch.calcMoney(
            Epoch.differenceBetweenTimestampsInDays(Epoch.now(), userWithStoreItems.user.start),
            userWithStoreItems.user.cigPerDay,
            userWithStoreItems.user.inPack,
            userWithStoreItems.user.price
        )

        moneySaved -= userWithStoreItems.storeItems
            .filter { it.bought }
            .sumOf { it.price.toDouble() }
            .toFloat()

        setCurrentMoney(moneySaved)

        adapter.setMoney(moneySaved)
        adapter.setItems(userWithStoreItems.storeItems.reversed())
    }

    private fun onDeleteItem(item: StoreItemEntity) {
        viewModel.removeStoreItem(item.id)
    }

    private fun onPurchaseItem(item: StoreItemEntity) {
        if (item.bought) {
            Toast.makeText(context, R.string.store_toast_item_bought, Toast.LENGTH_SHORT).show()
            return
        }

        when {
            item.price <= currentMoney -> viewModel.buyStoreItem(item.id)
            else -> Toast.makeText(context, R.string.store_toast_not_enough_money, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}