package com.example.quitnow.ui.home.goal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quitnow.R
import com.example.quitnow.databinding.FragmentGoalListDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class GoalDialogFragment : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "GoalDialogFragment"

        const val ARG_SELECTED_INDEX = "ARG_SELECTED_INDEX"

        fun newInstance(selectedIndex: Int): GoalDialogFragment {
            val args = Bundle().apply {
                putInt(ARG_SELECTED_INDEX, selectedIndex)
            }

            return GoalDialogFragment().apply {
                arguments = args
            }
        }
    }

    private var _binding: FragmentGoalListDialogBinding? = null
    private val binding: FragmentGoalListDialogBinding get() = _binding!!

    private var selectedIndex: Int = 0

    var onGoalSelected: (position: Int) -> Unit = { throw NotImplementedError("onGoalSelected not implemented!") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme)

        selectedIndex = arguments?.getInt(ARG_SELECTED_INDEX) ?: 0
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentGoalListDialogBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val items = listOf(
                resources.getString(R.string.goal_two_days),
                resources.getString(R.string.goal_three_days),
                resources.getString(R.string.goal_four_days),
                resources.getString(R.string.goal_five_days),
                resources.getString(R.string.goal_six_days),
                resources.getString(R.string.goal_one_week),
                resources.getString(R.string.goal_ten_days),
                resources.getString(R.string.goal_two_weeks),
                resources.getString(R.string.goal_three_weeks),
                resources.getString(R.string.goal_one_month),
                resources.getString(R.string.goal_three_months),
                resources.getString(R.string.goal_six_months),
                resources.getString(R.string.goal_one_year),
                resources.getString(R.string.goal_five_years),
            resources.getString(R.string.goal_ten_years)
        )

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = AdapterGoals(items, selectedIndex, this@GoalDialogFragment::onItemClicked)
        }
    }

    private fun onItemClicked(position: Int) {
        onGoalSelected.invoke(position)
        dismiss()
    }
}
