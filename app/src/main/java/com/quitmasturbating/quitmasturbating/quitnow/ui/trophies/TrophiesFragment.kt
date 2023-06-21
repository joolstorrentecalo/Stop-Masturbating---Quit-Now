package com.example.quitnow.ui.trophies

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quitnow.MainViewModel
import com.example.quitnow.R
import com.example.quitnow.databinding.FragmentTrophiesBinding

class TrophiesFragment : Fragment() {

    private var _binding: FragmentTrophiesBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTrophiesBinding.inflate(inflater, container, false)

        initRecycler()

        return binding.root
    }

    private fun initRecycler() {
        val mAdapter = AdapterTrophies(createTrophyItems())

        val gridLayoutManager = GridLayoutManager(context, 4)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (mAdapter.getItemViewType(position)) {
                    AdapterTrophies.DATA_ITEM -> 2
                    AdapterTrophies.SEPARATOR_ITEM -> 4
                    else -> throw UnknownError("Unknown view type.")
                }
            }

        }
        binding.recycler.run {
            layoutManager = gridLayoutManager
            adapter = mAdapter
        }
    }

    private fun createTrophyItems(): List<Trophy> {
        val cigNotSmoked = viewModel.calculateNotSmoked()
        val freeDays = viewModel.getSmokeFreeDays()
        val regainedDays = viewModel.getRegainedDays()

        val allItems = mutableListOf<Trophy>()

        allItems.add(Trophy.Separator(R.string.trophies_header_title_one))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_20, cigNotSmoked >= 20))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_100, cigNotSmoked >= 100))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_200, cigNotSmoked >= 200))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_500, cigNotSmoked >= 500))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_1000, cigNotSmoked >= 1000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_2000, cigNotSmoked >= 2000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_5000, cigNotSmoked >= 5000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_10000, cigNotSmoked >= 10000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_50000, cigNotSmoked >= 50000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_100000, cigNotSmoked >= 100000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_500000, cigNotSmoked >= 500000))
        allItems.add(Trophy.Data(R.drawable.cig_not_smoked_1000000, cigNotSmoked >= 1000000))

        allItems.add(Trophy.Separator(R.string.trophies_header_title_two))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_1, freeDays >= 1))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_3, freeDays >= 3))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_5, freeDays >= 5))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_7, freeDays >= 7))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_10, freeDays >= 10))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_14, freeDays >= 14))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_30, freeDays >= 30))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_90, freeDays >= 90))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_180, freeDays >= 180))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_365, freeDays >= 365))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_1000, freeDays >= 1000))
        allItems.add(Trophy.Data(R.drawable.smoke_free_days_3650, freeDays >= 3650))

        return allItems.toList()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
