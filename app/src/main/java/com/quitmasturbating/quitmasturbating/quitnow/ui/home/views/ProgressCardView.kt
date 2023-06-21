package com.example.quitnow.ui.home.views

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.animation.DecelerateInterpolator
import androidx.cardview.widget.CardView
import com.example.quitnow.R
import com.example.quitnow.databinding.MpCardProgressBinding


class ProgressCardView : CardView {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private lateinit var binding: MpCardProgressBinding

    var onSelectGoalClick: () -> Unit = { throw NotImplementedError("onSelectGoalClick not implemented") }

    private fun init() {
        binding = MpCardProgressBinding.inflate(LayoutInflater.from(context), this)

        binding.progressBar.isEnabled = false

        binding.btnSetGoal.setOnClickListener {
            onSelectGoalClick.invoke()
        }
    }

    fun setProgressValue(value: String) {
        binding.tvProgressValue.text = value
    }

    @SuppressLint("SetTextI18n")
    fun setGoalValue(value: String) {
        binding.tvGoal.text = "${context.resources.getString(R.string.goal_title)} $value"
    }

    fun setGoalPercentage(progress: Float) {
        setSeekBarValue(progress.toInt())
        setGoalPercentageValue(progress)
    }

    private fun setSeekBarValue(progress: Int) {
        ObjectAnimator.ofInt(binding.progressBar, "progress", binding.progressBar.progress, progress).apply {
            duration = 1000
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setGoalPercentageValue(progress: Float) {
        binding.tvGoalPercentage.text = when (progress >= 100) {
            true -> "100%"
            false -> "%.1f".format(progress) + "% Complete"
        }

    }
}