package com.example.quitnow.ui.settings

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object {
        const val TAG = "TimePickerFragment"

        fun newInstance(): TimePickerFragment {
            return TimePickerFragment()
        }
    }

    internal var onTimeSet: ((timestamp: Long) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)

        return TimePickerDialog(context, this, hour, minute, true)
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val timestamp = (hourOfDay * 60 * 60 * 1000 + minute * 60 * 1000).toLong()
        onTimeSet?.invoke(timestamp)
    }
}