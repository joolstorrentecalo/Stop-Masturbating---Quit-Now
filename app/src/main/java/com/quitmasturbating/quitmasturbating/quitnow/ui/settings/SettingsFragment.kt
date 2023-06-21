package com.example.quitnow.ui.settings

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.quitnow.MainViewModel
import com.example.quitnow.data.db.user.UserEntity
import com.example.quitnow.databinding.FragmentSettingsBinding
import com.example.quitnow.util.DateConverters.toDateTime
import com.example.quitnow.util.empty
import com.example.quitnow.util.hideKeyboard
import timber.log.Timber


class SettingsFragment : Fragment() {

    companion object {
        private const val DEFAULT_GOAL = 172800000L

        const val CURRENCY = ""
    }

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<MainViewModel>()

    private val listener = ViewTreeObserver.OnGlobalLayoutListener {
        view?.let {
            val r = Rect()
            it.getWindowVisibleDisplayFrame(r)

            val heightDiff = it.rootView.height - (r.bottom - r.top)
            if (heightDiff > 500) {
                Timber.d("Layout observer parent keyboard opened")
                if (binding.etYears.hasFocus() || binding.etPrice.hasFocus()) {
                    binding.scroller.scrollTo(0, binding.viewBorder.bottom)
                }
            } else {
                Timber.d("Layout observer parent keyboard closed")
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)

        viewModel.user.observe(viewLifecycleOwner) { user: UserEntity? ->
            when (user) {
                null -> {
                    binding.etDate.setText(String.empty)
                    binding.etCigPerDay.setText(String.empty)
                    binding.etCigInPack.setText(String.empty)
                    binding.etYears.setText(String.empty)
                    binding.etPrice.setText(String.empty)
                }
                else -> {
                    binding.etDate.setText(toDateTime(requireContext(), user.start))
                    binding.etCigPerDay.setText(user.cigPerDay.toString())
                    binding.etCigInPack.setText(user.inPack.toString())
                    binding.etYears.setText(user.years.toString())
                    binding.etPrice.setText(String.format("%.2f", user.price).replace(",", "."))
                }
            }
        }

        return binding.root
    }

    private fun showDateDialog() {
        val dialog = DatePickerFragment.newInstance()

        dialog.apply {
            onDateSet = this@SettingsFragment::onDateSet
        }

        dialog.show(childFragmentManager, DatePickerFragment.TAG)
    }

    override fun onResume() {
        super.onResume()
        view?.viewTreeObserver?.addOnGlobalLayoutListener(listener)
    }

    override fun onPause() {
        super.onPause()
        view?.viewTreeObserver?.removeOnGlobalLayoutListener(listener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etDate.showSoftInputOnFocus = false
        binding.etDate.setOnClickListener {
            hideKeyboard(requireContext(), it)
            showDateDialog()
        }

        binding.btnSubmit.setOnClickListener {
            val user = createUser() ?: return@setOnClickListener

            user.goal = user.start + DEFAULT_GOAL
            viewModel.setUserData(user, requireContext())
        }
    }

    private fun onDateSet(epoch: Long) {
        val dialog = TimePickerFragment.newInstance()

        dialog.apply {
            onTimeSet = { time ->
                this@SettingsFragment.onTimeSet(epoch + time)
            }
        }

        dialog.show(childFragmentManager, TimePickerFragment.TAG)
    }

    private fun onTimeSet(epoch: Long) {
        viewModel.setStartEpoch(epoch)
        binding.etDate.setText(toDateTime(requireContext(), epoch))
    }

    private fun createUser(): UserEntity? {
        val epoch = viewModel.getStartEpoch()
        if (epoch == 0L) return null

        val perDay = binding.etCigPerDay.text.toString().toInt()
        val inPack = binding.etCigInPack.text.toString().toInt()
        val years = binding.etYears.text.toString().toInt()
        val price = binding.etPrice.text.toString().run {
            if (!matches(Regex("[+-]?([0-9]*[.])?[0-9]+"))) {
                return null
            }
            toFloat()
        }

        return UserEntity(
            start = epoch,
            cigPerDay = perDay,
            inPack = inPack,
            years = years,
            price = price
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}